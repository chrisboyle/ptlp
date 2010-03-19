package name.boyle.chris.ptlp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.LinearLayout;

import com.twofortyfouram.SharedResources;

/**
 * This is the "Edit" activity for a <i>Locale</i> plug-in.
 */
public final class EditActivity extends Activity
{

	/**
	 * Menu ID of the save item.
	 */
	private static final int MENU_SAVE = 1;

	/**
	 * Menu ID of the don't save item.
	 */
	private static final int MENU_DONT_SAVE = 2;

	/**
	 * Flag boolean that can only be set to true via the "Don't Save" menu item in {@link #onMenuItemSelected(int, MenuItem)}. If
	 * true, then this {@code Activity} should return {@link Activity#RESULT_CANCELED} in {@link #finish()}.
	 * <p>
	 * There is no need to save/restore this field's state when the {@code Activity} is paused.
	 */
	private boolean isCancelled = false;
	
	ToggleButton tb;
	TextView hint;

	/** Called when the {@code Activity} is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		/*
		 * Locale guarantees that the breadcrumb string will be present, but checking for null anyway makes your Activity more
		 * robust and re-usable
		 */
		final String breadcrumbString = getIntent().getStringExtra(com.twofortyfouram.Intent.EXTRA_STRING_BREADCRUMB);
		if (breadcrumbString != null)
			setTitle(String.format("%s%s%s", breadcrumbString, com.twofortyfouram.Intent.BREADCRUMB_SEPARATOR, getString(R.string.plugin_name))); //$NON-NLS-1$

		/*
		 * Load the Locale background frame from Locale
		 */
		((LinearLayout) findViewById(R.id.frame)).setBackgroundDrawable(SharedResources.getDrawableResource(getPackageManager(), SharedResources.DRAWABLE_LOCALE_BORDER));

		tb = (ToggleButton)findViewById(R.id.toggle);
		hint = (TextView)findViewById(R.id.hint);
		tb.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				updateHint();
			}
		});
		
		/*
		 * if savedInstanceState == null, then we are entering the Activity directly from Locale and we need to check whether the
		 * Intent has forwarded a Bundle extra (e.g. whether we editing an old setting or creating a new one)
		 */
		if (savedInstanceState == null)
		{
			final Bundle forwardedBundle = getIntent().getBundleExtra(com.twofortyfouram.Intent.EXTRA_BUNDLE);

			/*
			 * the forwardedBundle would be null if this was a new setting
			 */
			if (forwardedBundle != null)
			{
				final boolean checked = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_TOGGLE, false);
				tb.setChecked(checked);
			}
		}
		/*
		 * if savedInstanceState != null, there is no need to restore any Activity state directly (e.g. onSaveInstanceState()).
		 * This is handled by the TextView automatically.
		 */
		updateHint();
	}
	
	public void updateHint()
	{
		hint.setText(getString(tb.isChecked() ? R.string.onBlurb : R.string.offBlurb));
	}

	/**
	 * Called when the {@code Activity} is being terminated. This method determines the state of the {@code Activity} and what
	 * sort of result should be returned to <i>Locale</i>.
	 */
	@Override
	public void finish()
	{
		if (isCancelled)
			setResult(RESULT_CANCELED);
		else
		{
			final boolean checked = ((ToggleButton) findViewById(R.id.toggle)).isChecked();

			/*
			 * This is the return Intent, into which we'll put all the required extras
			 */
			final Intent returnIntent = new Intent();

			/*
			 * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note that anything
			 * placed in this bundle must be available to Locale's class loader. So storing String, int, and other basic
			 * objects will work just fine. You cannot store an object that only exists in your project, as Locale will be
			 * unable to serialize it.
			 */
			final Bundle storeAndForwardExtras = new Bundle();
			storeAndForwardExtras.putBoolean(Constants.INTENT_EXTRA_TOGGLE, checked);

			returnIntent.putExtra(com.twofortyfouram.Intent.EXTRA_BUNDLE, storeAndForwardExtras);

			/*
			 * This is the blurb concisely describing what your setting's state is. This is simply used for display in the UI.
			 */
			String message = getString(checked ? R.string.onBlurb : R.string.offBlurb);
			if (message.length() > com.twofortyfouram.Intent.MAXIMUM_BLURB_LENGTH)
				returnIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BLURB, message.substring(0, com.twofortyfouram.Intent.MAXIMUM_BLURB_LENGTH));
			else
				returnIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BLURB, message);

			setResult(RESULT_OK, returnIntent);
		}

		super.finish();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		final PackageManager manager = getPackageManager();

		final Intent helpIntent = new Intent(com.twofortyfouram.Intent.ACTION_HELP);
		helpIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_HELP_URL, getString(R.string.website)); //$NON-NLS-1$

		// Note: title was set in onCreate
		helpIntent.putExtra(com.twofortyfouram.Intent.EXTRA_STRING_BREADCRUMB, getTitle().toString());

		/*
		 * We are dynamically loading resources from Locale's APK. This will only work if Locale is actually installed
		 */
		menu.add(SharedResources.getTextResource(manager, SharedResources.STRING_MENU_HELP))
			.setIcon(SharedResources.getDrawableResource(manager, SharedResources.DRAWABLE_MENU_HELP)).setIntent(helpIntent);

		menu.add(0, MENU_DONT_SAVE, 0, SharedResources.getTextResource(manager, SharedResources.STRING_MENU_DONTSAVE))
			.setIcon(SharedResources.getDrawableResource(manager, SharedResources.DRAWABLE_MENU_DONTSAVE)).getItemId();

		menu.add(0, MENU_SAVE, 0, SharedResources.getTextResource(manager, SharedResources.STRING_MENU_SAVE))
			.setIcon(SharedResources.getDrawableResource(manager, SharedResources.DRAWABLE_MENU_SAVE)).getItemId();

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item)
	{
		switch (item.getItemId())
		{
			case MENU_SAVE:
			{
				finish();
				return true;
			}
			case MENU_DONT_SAVE:
			{
				isCancelled = true;
				finish();
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}
}
