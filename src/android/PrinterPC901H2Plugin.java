package tj.ehdos.cordova.plugin;
// The native Toast API
import android.widget.Toast;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

//import com.smartdevice.aidl.ICallBack;
import com.smartdevice.aidl.IZKCService;

public class PrinterPC901H2Plugin extends CordovaPlugin {
	
  public static String MODULE_FLAG = "module_flag";
  public static int module_flag = 0;
  public static int DEVICE_MODEL = 0;
  private Handler mhanlder;	
	

  private static final String DURATION_LONG = "long";
  @Override
  public boolean execute(String action, JSONArray args,
    final CallbackContext callbackContext) {
      // Verify that the user sent a 'show' action

    if(action.equals("load"))
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        module_flag = getIntent().getIntExtra(MODULE_FLAG, 8);
        //bind service
        bindService();
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(pluginResult);
        return true;
    }
    else if(action.equals("print"))
    {
      String message;
      try {
        JSONObject options = args.getJSONObject(0);
        message = options.getString("message");
      } catch (JSONException e) {
        callbackContext.error("Error encountered: " + e.getMessage());
        return false;
      }
      // Create the toast
      Toast toast = Toast.makeText(cordova.getActivity(), "OK",
        DURATION_LONG.equals(duration) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
      // Display toast
	  
	  printGBKText(message);
      toast.show();
      // Send a positive result to the callbackContext
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
      callbackContext.sendPluginResult(pluginResult);
      return true;
    }
  }
  
  
  protected Handler getHandler() {
        if (mhanlder == null) {
            mhanlder = new Handler() {
                public void handleMessage(Message msg) {
                    handleStateMessage(msg);
                }
            };
        }
        return mhanlder;
    }

    protected void handleStateMessage(Message message) {}

    protected void sendMessage(Message message) {
        getHandler().sendMessage(message);
    }

    protected void sendMessage(int what, Object obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        getHandler().sendMessage(message);
    }

    protected void sendEmptyMessage(int what) {
        getHandler().sendEmptyMessage(what);
    }

    public static IZKCService mIzkcService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("client", "onServiceDisconnected");
            mIzkcService = null;
            Toast.makeText(MainActivity.this, "Error connect aidl", Toast.LENGTH_SHORT).show();
            
            sendEmptyMessage(0);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("client", "onServiceConnected");
            mIzkcService = IZKCService.Stub.asInterface(service);
            if(mIzkcService!=null){
                try {
                    Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    DEVICE_MODEL = mIzkcService.getDeviceModel();
                    mIzkcService.setModuleFlag(module_flag);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                sendEmptyMessage(1);
            }
        }
    };

    public void bindService() {
        //com.zkc.aidl.all
        //com.smartdevice.aidl

        Intent intent = new Intent("com.zkc.aidl.all");
        intent.setPackage("com.smartdevice.aidl");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        unbindService(mServiceConn);
    }

   
    

    private void printPrinter() throws RemoteException {
        //mIzkcService.printerInit();
        //mIzkcService.Open();
        //mIzkcService.generateSpace();
        //IZKCService mIzkcService = new IZKCService();

    }

    private void printGBKText(String text) {

        try {
            mIzkcService.printUnicode_1F30(text);
            mIzkcService.generateSpace();
        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }

    synchronized public void printPurchaseBillModelOne(
            IZKCService mIzkcService) {

        try {
            if (mIzkcService!=null&&mIzkcService.checkPrinterAvailable()) {
                mIzkcService.printGBKText("\n\n");

                SystemClock.sleep(50);
//				mIzkcService.printGBKText("\n");






                //mIzkcService.printGBKText(mIzkcService_CUT_OFF_RULE);


            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
  
}