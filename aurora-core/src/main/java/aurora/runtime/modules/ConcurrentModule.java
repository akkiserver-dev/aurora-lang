package aurora.runtime.modules;

import aurora.runtime.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Native module providing primitives for concurrent programming in Aurora.
 * This includes support for waiting on futures and mutex-based synchronization.
 */
@AuroraLib("Aurora.Runtime.Concurrent")
public class ConcurrentModule implements NativeModule {
    @Override
    public void register(VM vm) {
        NativeBinder.bind(vm, this);
    }

    @AuroraNative("Aurora.Runtime.Concurrent.Future.wait(LFuture;)")
    public ArObject wait(ArFuture future) {
        return future.waitValue();
    }

    @AuroraNative("Aurora.Runtime.Concurrent.Mutex.__native_mutex_lock(LMutex;)")
    public void lock(ArInstance mutexInstance) {
        ArObject lockObj = mutexInstance.fields.get("__lock");
        ReentrantLock lock;
        if (lockObj instanceof ArNativeObject && ((ArNativeObject)lockObj).object instanceof ReentrantLock) {
            lock = (ReentrantLock) ((ArNativeObject)lockObj).object;
        } else {
            lock = new ReentrantLock();
            mutexInstance.fields.put("__lock", new ArNativeObject(lock));
        }
        lock.lock();
    }

    @AuroraNative("Aurora.Runtime.Concurrent.Mutex.__native_mutex_unlock(LMutex;)")
    public void unlock(ArInstance mutexInstance) {
        ArObject lockObj = mutexInstance.fields.get("__lock");
        if (lockObj instanceof ArNativeObject && ((ArNativeObject)lockObj).object instanceof ReentrantLock) {
            ((ReentrantLock)((ArNativeObject)lockObj).object).unlock();
        }
    }
}
