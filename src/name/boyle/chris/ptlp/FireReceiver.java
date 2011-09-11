package name.boyle.chris.ptlp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

/**
 * This is the "fire" {@code BroadcastReceiver} for a <i>Locale</i> plug-in setting.
 */
public final class FireReceiver extends BroadcastReceiver
{

	/**
	 * @param context {@inheritDoc}.
	 * @param intent the incoming {@code Intent}. This should always contain the store-and-forward {@code Bundle} that was saved
	 *            by {@link EditActivity} and later broadcast by <i>Locale</i>.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		/*
		 * Always be sure to be strict on your input parameters! A malicious third-party app could always send your plug-in an
		 * empty or otherwise malformed Intent. And since Locale applies settings in the background, you don't want your plug-in
		 * to crash.
		 */
		if (com.twofortyfouram.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
		{
			final Bundle forwardedBundle = intent.getBundleExtra(com.twofortyfouram.Intent.EXTRA_BUNDLE);
			if (forwardedBundle == null) return;
			final boolean toggle = forwardedBundle.getBoolean(Constants.INTENT_EXTRA_TOGGLE, false);
			Settings.System.putInt(context.getContentResolver(), "notification_light_pulse", toggle ? 1 : 0);
		}
	}
}
