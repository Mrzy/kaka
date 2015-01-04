package cn.zmdx.kaka.locker.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private static String APPID = "wx5fa094ca2b1994ba";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, APPID, true);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToSplash();
			// 在微信中点击豆丁书房
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToBookReader((ShowMessageFromWX.Req) req);
			// 发送方,接收方点击
			break;
		default:
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
		    Log.d("syc", "发送成功！");
			finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
		    Log.d("syc", "发送shibai！");
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
		    Log.d("syc", "发送beijijue！");
			finish();
			break;
		}

	}

	private void goToSplash() {
		finish();
	}

	private void goToBookReader(ShowMessageFromWX.Req showReq) {
		finish();
	}
}