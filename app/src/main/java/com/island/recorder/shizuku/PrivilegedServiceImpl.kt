package com.island.recorder.shizuku

import android.os.IBinder
import android.util.Log
import com.island.recorder.IPrivilegedService

class PrivilegedServiceImpl : IPrivilegedService.Stub() {

    private val TAG = "PrivilegedService"
    private val CHAIN_OEM_DENY_3 = 9

    private val cmInstance: Any? by lazy {
        try {
            val smClass = Class.forName("android.os.ServiceManager")
            val getService = smClass.getMethod("getService", String::class.java)
            val binder = getService.invoke(null, "connectivity") as IBinder
            
            val stubClass = Class.forName("android.net.IConnectivityManager\$Stub")
            val asInterface = stubClass.getMethod("asInterface", IBinder::class.java)
            asInterface.invoke(null, binder)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get IConnectivityManager: ${e.message}")
            null
        }
    }

    override fun setPackageNetworkingEnabled(uid: Int, enabled: Boolean): Boolean {
        val cm = cmInstance ?: return false
        return try {
            val rule = if (enabled) 0 else 2 // 0: DEFAULT, 2: DENY
            
            if (!enabled) {
                callMethod(cm, "setFirewallChainEnabled", arrayOf(Int::class.javaPrimitiveType!!, Boolean::class.javaPrimitiveType!!), arrayOf(CHAIN_OEM_DENY_3, true))
            }
            
            callMethod(cm, "setUidFirewallRule", arrayOf(Int::class.javaPrimitiveType!!, Int::class.javaPrimitiveType!!, Int::class.javaPrimitiveType!!), arrayOf(CHAIN_OEM_DENY_3, uid, rule))
            
            Log.d(TAG, "Set UID $uid networking to $enabled")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set networking: ${e.message}")
            false
        }
    }

    private fun callMethod(obj: Any, methodName: String, types: Array<Class<*>>, args: Array<Any>): Any? {
        val method = obj.javaClass.getMethod(methodName, *types)
        return method.invoke(obj, *args)
    }
}
