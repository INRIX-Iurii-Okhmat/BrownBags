package com.iuriioapps.wearcommon;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.wearable.Wearable.API;
import static com.google.android.gms.wearable.Wearable.CapabilityApi;
import static com.google.android.gms.wearable.Wearable.DataApi;

/**
 * Simplifies work with Wear data transfer API.
 */
public enum WearConnector {
    INSTANCE;

    /**
     * Connection callbacks.
     */
    public interface IConnectionListener {
        /**
         * Called when client is connected.
         */
        void onConnected();

        /**
         * Called when client is disconnected.
         */
        void onDisconnected();
    }

    /**
     * Callback interface called when Wear data changes.
     */
    public interface IWearDataChangedListener {
        /**
         * Called when wear data changes.
         *
         * @param path Data path.
         * @param data {@link Bundle} containing received data.
         */
        void onWearDataChanged(final String path, final Bundle data);
    }

    /**
     * Callback interface called when Wear asset changes.
     */
    public interface IWearAssetChangedListener {
        /**
         * Called when wear asset chnages.
         *
         * @param path  Data path.
         * @param name  Asset name.
         * @param asset {@link Bitmap} asset.
         */
        void onAssetChanged(final String path, final String name, final Bitmap asset);
    }

    /**
     * Callback interface called when node lookup completed.
     */
    public interface IWearNodeLookupListener {
        /**
         * Called when a node lookup completed.
         *
         * @param id Node ID if found; otherwise null.
         */
        void onNodeFound(final String id);
    }

    /**
     * Callback interface called when Wear message received.
     */
    public interface IWearMessageReceivedListener {
        /**
         * Called when a wear message received.
         *
         * @param path    Message path.
         * @param message Message text.
         */
        void onMessageReceived(final String path, final String message);
    }

    private static final Logger logger = LoggerFactory.getLogger(WearConnector.class);

    final class ConnectionHandler implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public final void onConnected(final Bundle connectionHint) {
            logger.trace("Connected - {}", connectionHint);
            WearConnector.this.onConnected();
        }

        @Override
        public final void onConnectionSuspended(final int status) {
            logger.trace("Connection suspended");
            WearConnector.this.onConnectionSuspended();
        }

        @Override
        public final void onConnectionFailed(final ConnectionResult connectionResult) {
            logger.trace("Connection failed: {}", connectionResult);
            WearConnector.this.onConnectionFailed();
        }
    }

    final class DataListener implements DataApi.DataListener {
        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            logger.trace("New data received.");

            final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

            WearConnector.this.onDataChanged(events);
        }
    }

    private ExecutorService taskExecutor;

    private PackageManager pm;
    private Resources resources;
    private GoogleApiClient client;

    private IConnectionListener connectionListener;
    private ConnectionHandler clientConnectionHandler = new ConnectionHandler();
    private DataListener wearDataListener;

    private final MessageApi.MessageListener wearMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            messageReceived(messageEvent);
        }
    };

    private final Map<String, List<WeakReference<IWearDataChangedListener>>> dataChangeListeners;
    private final Map<String, Map<String, WeakReference<IWearAssetChangedListener>>> assetChangeListeners;
    private final Map<String, List<WeakReference<IWearMessageReceivedListener>>> messageReceivedListeners;

    /**
     * Initializes a new instance of the {@link WearConnector}.
     */
    WearConnector() {
        this.dataChangeListeners = Collections.synchronizedMap(new HashMap<String, List<WeakReference<IWearDataChangedListener>>>());
        this.assetChangeListeners = Collections.synchronizedMap(new HashMap<String, Map<String, WeakReference<IWearAssetChangedListener>>>());
        this.messageReceivedListeners = Collections.synchronizedMap(new HashMap<String, List<WeakReference<IWearMessageReceivedListener>>>());
    }

    /**
     * Sets the new connection listener.
     *
     * @param listener An instance of {@link com.iuriioapps.wearcommon.WearConnector.IConnectionListener}.
     */
    public void setConnectionListener(final IConnectionListener listener) {
        this.connectionListener = listener;
    }

    /**
     * Add new data change listener.
     *
     * @param path     Data path.
     * @param listener An instance of {@link com.iuriioapps.wearcommon.WearConnector.IWearDataChangedListener}.
     * @throws IllegalArgumentException Thrown when path is null or empty or when it doesn't start with "/".
     */
    @SuppressWarnings("unused")
    public void addDataChangeListener(@NonNull final String path, @NonNull final IWearDataChangedListener listener) {
        verifyPath(path);

        if (!this.dataChangeListeners.containsKey(path)) {
            this.dataChangeListeners.put(
                    path,
                    new CopyOnWriteArrayList<WeakReference<IWearDataChangedListener>>());
        }

        this.dataChangeListeners.get(path).add(new WeakReference<>(listener));
        logger.trace("New data change listener register for path '{}'", path);

        this.ensureDataListener();
    }

    /**
     * Remove data change listener with specified path.
     *
     * @param path     Data path.
     * @param listener Listener instance to remove.
     * @throws IllegalArgumentException Thrown when path is null or empty.
     */
    @SuppressWarnings("unused")
    public void removeDataChangeListener(@NonNull final String path, @NonNull final IWearDataChangedListener listener) {
        verifyPath(path);

        final List<WeakReference<IWearDataChangedListener>> listeners = this.dataChangeListeners.get(path);
        if (listeners == null) {
            return;
        }

        for (final WeakReference<IWearDataChangedListener> listenerRef : listeners) {
            final IWearDataChangedListener instance = listenerRef.get();
            if (instance != null && instance.equals(listener)) {
                listeners.remove(listenerRef);
            }
        }

        if (listeners.isEmpty()) {
            this.dataChangeListeners.remove(path);
        }
    }

    /**
     * Add new asset change listener.
     *
     * @param path     Data path.
     * @param name     Asset name.
     * @param listener An instance of {@link com.iuriioapps.wearcommon.WearConnector.IWearAssetChangedListener}.
     * @throws IllegalArgumentException Thrown when path is null or empty or when it doesn't start with "/".
     */
    @SuppressWarnings("unused")
    public void addAssetChangeListener(
            @NonNull final String path,
            @NonNull final String name,
            @NonNull final IWearAssetChangedListener listener) {
        verifyPath(path);

        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name can't be null.");
        }

        if (!this.assetChangeListeners.containsKey(path)) {
            this.assetChangeListeners.put(
                    path,
                    new HashMap<String, WeakReference<IWearAssetChangedListener>>());
        }

        this.assetChangeListeners.get(path).put(name, new WeakReference<>(listener));
        logger.trace("New asset change listener register for path '{}'", path);

        this.ensureDataListener();
    }

    /**
     * Remove asset change listener with specified path.
     *
     * @param path     Data path.
     * @param name     Asset name.
     * @param listener Listener instance to remove.
     * @throws IllegalArgumentException Thrown when path is null or empty.
     */
    @SuppressWarnings("unused")
    public void removeAssetChangeListener(
            @NonNull final String path,
            @NonNull final String name,
            @NonNull final IWearAssetChangedListener listener) {
        this.verifyPath(path);

        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name can't be null.");
        }

        final Map<String, WeakReference<IWearAssetChangedListener>> listenersMap = this.assetChangeListeners.get(path);
        if (listenersMap == null) {
            return;
        }

        final WeakReference<IWearAssetChangedListener> listenerRef = listenersMap.get(name);
        if (listenerRef == null) {
            return;
        }

        if (listenerRef.get() == null || (listenerRef.get() != null && listenerRef.get().equals(listener))) {
            listenersMap.remove(name);
        }

        if (listenersMap.isEmpty()) {
            this.dataChangeListeners.remove(path);
        }
    }

    /**
     * Add new message listener.
     *
     * @param path     Message path.
     * @param listener An instance of {@link IWearMessageReceivedListener}.
     * @throws IllegalArgumentException Thrown when path is null or empty or when it doesn't start with "/".
     */
    @SuppressWarnings("unused")
    public void addMessageListener(@NonNull final String path, @NonNull final IWearMessageReceivedListener listener) {
        this.verifyPath(path);

        if (!this.messageReceivedListeners.containsKey(path)) {
            this.messageReceivedListeners.put(
                    path,
                    new CopyOnWriteArrayList<WeakReference<IWearMessageReceivedListener>>());
        }

        this.messageReceivedListeners.get(path).add(new WeakReference<>(listener));
        logger.trace("New message listener register for path '{}'", path);

        this.ensureMessageListener();
    }

    /**
     * Remove message listener with specified path.
     *
     * @param path     Message path.
     * @param listener Listener instance to remove.
     * @throws IllegalArgumentException Thrown when path is null or empty.
     */
    @SuppressWarnings("unused")
    public void removeMessageListener(@NonNull final String path, @NonNull final IWearMessageReceivedListener listener) {
        this.verifyPath(path);

        final List<WeakReference<IWearMessageReceivedListener>> listeners = this.messageReceivedListeners.get(path);
        if (listeners == null) {
            return;
        }

        for (final WeakReference<IWearMessageReceivedListener> listenerRef : listeners) {
            final IWearMessageReceivedListener instance = listenerRef.get();
            if (instance != null && instance.equals(listener)) {
                listeners.remove(listenerRef);
            }
        }

        if (listeners.isEmpty()) {
            this.dataChangeListeners.remove(path);
        }
    }

    /**
     * Post new data.
     *
     * @param path Data path.
     * @param data {@link Bundle} that contains the data.
     * @throws IllegalArgumentException Thrown when path is null or empty.
     * @throws IllegalArgumentException Thrown when path doesn't start with "/".
     * @throws IllegalArgumentException Thrown when data is null or empty.
     */
    public void postData(@NonNull final String path, @NonNull final Bundle data) {
        verifyConnected();
        verifyPath(path);

        //noinspection ConstantConditions
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data can't be null or empty");
        }

        final DataMap map = DataMap.fromBundle(data);
        final PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
        dataMapRequest.getDataMap().putAll(map);

        final PutDataRequest dataRequest = dataMapRequest.asPutDataRequest();

        // Play Services 8.3 addition - more here:
        // http://android-developers.blogspot.com/2015/11/whats-new-in-google-play-services-83.html
        // TODO: There should be a method parameter that controls this.
        dataRequest.setUrgent();
        final PutDataTask task = new PutDataTask(this.client, dataRequest);
        this.executeAsync(task);

        logger.trace("Data posted: {}", dataRequest);
    }

    /**
     * Post drawable asset.
     *
     * @param path  Data path.
     * @param name  Name of the drawable asset.
     * @param resId Drawable resource ID.
     */
    @SuppressWarnings("unused")
    public void postAsset(@NonNull final String path, @NonNull final String name, @DrawableRes final int resId) {
        verifyConnected();

        final Bitmap bitmap = BitmapFactory.decodeResource(this.resources, resId);
        this.postAsset(path, name, bitmap);
    }

    /**
     * Post bitmap asset.
     *
     * @param path   Data path value.
     * @param name   Name of the bitmap asset.
     * @param bitmap {@link Bitmap} instance that should be transfered.
     */
    public void postAsset(@NonNull final String path, @NonNull final String name, @NonNull final Bitmap bitmap) {
        verifyConnected();
        verifyPath(path);

        //noinspection ConstantConditions
        if (bitmap == null || bitmap.isRecycled()) {
            throw new InvalidParameterException("Bitmap can't be null or recycled.");
        }

        // Create asset from specified bitmap.
        this.executeAsync(new Runnable() {
            @Override
            public void run() {
                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

                try {
                    byteStream.flush();
                } catch (final IOException ignored) {
                }

                final Asset asset = Asset.createFromBytes(byteStream.toByteArray());

                try {
                    byteStream.close();
                } catch (final IOException ignored) {
                    // Not a big deal, as it's in-memory stream anyway.
                }

                // Create and post asset data request.
                final PutDataMapRequest dataMap = PutDataMapRequest.create(path);
                dataMap.getDataMap().putAsset(name, asset);

                // TODO: Find out how to read cached assets.
                // TODO: Remove this, otherwise it will cause retransmission
                // even if the data didn't change.
                dataMap.getDataMap().putLong("timestamp", System.currentTimeMillis());

                final PutDataRequest request = dataMap.asPutDataRequest();
                executeAsync(new PutDataTask(client, request));

                logger.trace("Asset posted: {}", request.toString(true));

            }
        });
    }

    /**
     * Send a message to a connected node.
     *
     * @param path    Message path.
     * @param message Message content (up 100Kb in size).
     */
    public void sendMessage(@NonNull final String path, @NonNull final String message) {
        this.findConnectedNode(new IWearNodeLookupListener() {
            @Override
            public void onNodeFound(final String nodeId) {
                sendMessage(nodeId, path, message);
            }
        });
    }

    /**
     * Send a message to a connected node.
     *
     * @param nodeId  Target node ID.
     * @param path    Message path.
     * @param message Message content (up 100Kb in size).
     */
    public void sendMessage(
            @NonNull final String nodeId,
            @NonNull final String path,
            @NonNull final String message) {
        this.verifyConnected();
        this.verifyPath(path);

        logger.trace("Sending message {}::{} to {}", path, message, nodeId);

        Wearable.MessageApi.sendMessage(this.client, nodeId, path, message.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(final MessageApi.SendMessageResult result) {
                if (result.getStatus().isSuccess()) {
                    logger.trace("Message sent: {}", result);
                } else {
                    logger.error("Failed to send message: {}", result);
                }
            }
        });
    }

    /**
     * Find nearby connected node with given capability.
     *
     * @param name     Capability name.
     * @param listener Callback instance.
     */
    public void findNodeWithCapability(@NonNull final String name, @NonNull final IWearNodeLookupListener listener) {
        this.verifyConnected();

        //noinspection ConstantConditions
        if (listener == null) {
            return;
        }

        //noinspection AccessStaticViaInstance
        Wearable.CapabilityApi.getCapability(this.client, name, CapabilityApi.FILTER_REACHABLE)
                .setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
                    @Override
                    public void onResult(final CapabilityApi.GetCapabilityResult result) {
                        if (!result.getStatus().isSuccess()) {
                            // Node discovery failed.
                            listener.onNodeFound(null);
                            return;
                        }

                        final Set<Node> nodes = result.getCapability().getNodes();
                        final String nodeId = pickBestNodeId(nodes);
                        listener.onNodeFound(nodeId);
                    }
                });
    }

    /**
     * Obtain the first connected node.
     *
     * @param listener Callback listener instance.
     */
    public void findConnectedNode(final @NonNull IWearNodeLookupListener listener) {
        this.verifyConnected();

        Wearable.NodeApi.getConnectedNodes(this.client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(final NodeApi.GetConnectedNodesResult result) {
                if (!result.getStatus().isSuccess()) {
                    logger.warn("Node not found.");
                    listener.onNodeFound(null);
                    return;
                }

                // TODO: Due to inconsistencies in CapabilitiesApi and NodeApi responses, convert
                // List<Node> into Set<Node>.

                final Set<Node> nodes = new HashSet<>(result.getNodes());
                final String node = pickBestNodeId(nodes);

                logger.trace("Selected node ID: {}", node);
                listener.onNodeFound(node);
            }
        });
    }

    /**
     * Find a best node ID, pick a nearby node or arbitrary node if there is no nearby nodes.
     *
     * @param nodes A set of nodes to go through.
     * @return Selected node ID.
     */
    private String pickBestNodeId(final Set<Node> nodes) {
        String bestNodeId = null;

        for (final Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }

        return bestNodeId;
    }

    /**
     * Checks if GPS is supported by this device.
     *
     * @return True if GPS is supported; otherwise false.
     */
    public boolean hasGps() {
        return this.pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    /**
     * Gets the current location information.
     *
     * @return Current location, if available and client is connected; otherwise null.
     */
    public Location getCurrentLocation() {
        if (!this.isConnected()) {
            return null;
        }

        return LocationServices.FusedLocationApi.getLastLocation(this.client);
    }

    /**
     * Start new connection.
     *
     * @param context Current context instance.
     */
    public final void connect(@NonNull final Context context) {
        logger.trace("Connecting.");

        this.pm = context.getApplicationContext().getPackageManager();
        this.resources = context.getApplicationContext().getResources();

        if (this.client == null) {
            this.client = new GoogleApiClient.Builder(context.getApplicationContext())
                    .addConnectionCallbacks(this.clientConnectionHandler)
                    .addOnConnectionFailedListener(this.clientConnectionHandler)
                    .addApi(LocationServices.API)
                    .addApi(API)
                    .build();

            logger.trace("New client created.");
        }

        this.client.connect();
    }

    /**
     * Terminate current connection.
     */
    public final void disconnect() {
        if (!this.isConnected()) {
            return;
        }

        logger.trace("Remove data listener.");
        DataApi.removeListener(this.client, this.wearDataListener);
        this.wearDataListener = null;

        logger.trace("Remove message listener.");
        Wearable.MessageApi.removeListener(this.client, this.wearMessageListener);

        this.client.disconnect();
        logger.trace("Disconnected.");

        if (this.connectionListener != null) {
            this.connectionListener.onDisconnected();
        }

        if (this.taskExecutor != null && !this.taskExecutor.isTerminated()) {
            this.taskExecutor.shutdown();
            try {
                this.taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (final InterruptedException ignored) {
            }

            this.taskExecutor = null;
        }
    }

    /**
     * Gets the value indicating whether we're currently connected to the wear client API.
     *
     * @return {@code true} if we're connected; otherwise {@code false}.
     */
    public final boolean isConnected() {
        return this.client != null && (this.client.isConnected() || this.client.isConnecting());
    }

    /**
     * Called when {@link GoogleApiClient} connection established.
     */
    private void onConnected() {
        if (this.taskExecutor == null) {
            this.taskExecutor = Executors.newCachedThreadPool();
        }

        if (this.connectionListener != null) {
            this.connectionListener.onConnected();
        }

        // Register data listener (if needed).
        this.ensureDataListener();

        // Register message listener.
        this.ensureMessageListener();
    }

    /**
     * Called when {@link GoogleApiClient} connection suspended.
     */
    private void onConnectionSuspended() {
    }

    /**
     * Called when {@link GoogleApiClient} connection failed.
     */
    private void onConnectionFailed() {
    }

    /**
     * Ensure that global data listener is set.
     */
    private void ensureDataListener() {
        final boolean hasDataListeners = this.dataChangeListeners.size() > 0;
        final boolean hasAssetListeners = this.assetChangeListeners.size() > 0;
        final boolean hasListeners = hasDataListeners || hasAssetListeners;

        if (this.isConnected() && hasListeners) {
            logger.trace("Explicitly registering global data change listener.");
            this.wearDataListener = new DataListener();
            DataApi.addListener(this.client, this.wearDataListener);
        }
    }

    /**
     * Ensure that global message listener is set.
     */
    private void ensureMessageListener() {
        final boolean hasMessageListeners = this.messageReceivedListeners.size() > 0;

        if (this.isConnected() && hasMessageListeners) {
            logger.trace("Explicitly registering global message listener.");
            Wearable.MessageApi.addListener(this.client, this.wearMessageListener);
        }
    }

    /**
     * Called when client receives a change in Wear data stream.
     *
     * @param events Received data events.
     */
    private void onDataChanged(final List<DataEvent> events) {
        for (final DataEvent event : events) {
            final int eventType = event.getType();
            final String path = event.getDataItem().getUri().getPath();
            logger.trace("Processing event - type: {}, path: {}", eventType, path);

            final List<WeakReference<IWearDataChangedListener>> dataListeners = this.dataChangeListeners.get(path);
            final Map<String, WeakReference<IWearAssetChangedListener>> assetListeners = this.assetChangeListeners.get(path);

            final boolean hasDataListeners = dataListeners != null && !dataListeners.isEmpty();
            final boolean hasAssetListeners = assetListeners != null && !assetListeners.isEmpty();
            final boolean hasListeners = hasDataListeners || hasAssetListeners;

            if (!hasListeners) {
                logger.trace("No listeners registered for this event.");
                continue;
            }

            switch (eventType) {
                case DataEvent.TYPE_CHANGED:
                    final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    // Process data listeners.
                    if (hasDataListeners) {
                        for (final WeakReference<IWearDataChangedListener> listenerRef : dataListeners) {
                            final IWearDataChangedListener dataChangedListener = listenerRef.get();
                            if (dataChangedListener != null) {
                                dataChangedListener.onWearDataChanged(path, dataMap.toBundle());
                            }
                        }
                    }

                    // Process asset listeners.
                    if (hasAssetListeners) {
                        for (final String key : assetListeners.keySet()) {
                            final IWearAssetChangedListener listener = assetListeners.get(key).get();
                            if (listener == null) {
                                continue;
                            }

                            final Asset asset = dataMap.getAsset(key);
                            if (asset == null) {
                                continue;
                            }

                            this.executeAsync(new LoadAssetTask(
                                    this.client,
                                    path,
                                    key,
                                    asset,
                                    listener));
                        }
                    }
                    break;
                default:
                    logger.warn("Event type is unknown or not supported.");
                    break;
            }
        }
    }

    /**
     * Called when a new {@link MessageEvent} received from {@link Wearable#MessageApi}.
     *
     * @param event Event instance.
     */
    private void messageReceived(final MessageEvent event) {
        final String path = event.getPath();

        final List<WeakReference<IWearMessageReceivedListener>> listeners = this.messageReceivedListeners.get(path);
        if (listeners == null || listeners.size() == 0) {
            logger.trace("No message listeners registered for path: {}", path);
            return;
        }

        for (final WeakReference<IWearMessageReceivedListener> listenerRef : listeners) {
            final IWearMessageReceivedListener listener = listenerRef.get();
            if (listener == null) {
                continue;
            }

            final String message = new String(event.getData());
            listener.onMessageReceived(path, message);
        }
    }

    /**
     * Common method to verify data path.
     *
     * @param path Data path value.
     */
    private void verifyPath(final String path) {
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Path can't be null or empty");
        }

        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path should start with /");
        }
    }

    /**
     * Common method to verify whether the client is connected.
     */
    private void verifyConnected() {
        if (!this.isConnected()) {
            throw new IllegalStateException("Client is not connected. Make sure to call connect first.");
        }
    }

    /**
     * Run specified task asynchronously.
     *
     * @param task Task instance.
     */
    private void executeAsync(final Runnable task) {
        if (this.taskExecutor == null || this.taskExecutor.isTerminated()) {
            logger.warn("Task executor terminated, can't execute tasks");
            return;
        }

        this.taskExecutor.execute(task);
    }

    /**
     * Async task to post wear data.
     */
    private static final class PutDataTask implements Runnable {
        private final PutDataRequest request;
        private final GoogleApiClient client;

        /**
         * Initializes a new instance of the {@link PutDataRequest}.
         *
         * @param client  An instance of {@link GoogleApiClient}.
         * @param request An instance of {@link PutDataRequest} that should be posted.
         */
        public PutDataTask(
                final GoogleApiClient client,
                final PutDataRequest request) {
            this.client = client;
            this.request = request;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            final DataApi.DataItemResult result = DataApi.putDataItem(this.client, request).await();
            if (result.getStatus().isSuccess()) {
                logger.trace("Sent: {}", result);
            } else {
                logger.error("Send failed: {}", result);
            }
        }
    }

    /**
     * Async task to load assets.
     */
    private static final class LoadAssetTask implements Runnable {
        private final GoogleApiClient client;
        private final String path;
        private final String key;
        private final Asset asset;
        private final IWearAssetChangedListener listener;

        /**
         * Initializes a new instance of the {@link com.iuriioapps.wearcommon.WearConnector.LoadAssetTask}.
         *
         * @param client   Current {@link GoogleApiClient} instance.
         * @param path     Data path.
         * @param key      Data key.
         * @param asset    {@link Asset} that should be loaded.
         * @param listener Callback instance.
         */
        public LoadAssetTask(
                final GoogleApiClient client,
                final String path,
                final String key,
                final Asset asset,
                final IWearAssetChangedListener listener) {
            this.client = client;
            this.path = path;
            this.key = key;
            this.asset = asset;
            this.listener = listener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            final Bitmap bitmap = this.loadBitmapFromAsset(this.asset);
            if (bitmap == null) {
                logger.warn("Asset wasn't load for {}/{}", this.path, this.key);
                return;
            }

            this.listener.onAssetChanged(this.path, this.key, bitmap);
        }

        /**
         * Load {@link Bitmap} from {@link Asset}.
         *
         * @param asset Asset instance to load bitmap from.
         * @return {@link Bitmap} instance, if loaded successfully; otherwise null.
         */
        private Bitmap loadBitmapFromAsset(@NonNull final Asset asset) {
            final InputStream assetInputStream = DataApi.getFdForAsset(this.client, asset).await().getInputStream();

            if (assetInputStream == null) {
                logger.warn("Requested an unknown asset.");
                return null;
            }

            final Bitmap result = BitmapFactory.decodeStream(assetInputStream);

            try {
                assetInputStream.close();
            } catch (final IOException e) {
                logger.warn("Failed to close asset input stream", e);
            }

            return result;
        }
    }

}