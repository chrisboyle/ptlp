package name.boyle.chris.ptlp;

/**
 * Class of {@code Intent} constants used by this <i>Locale</i> plug-in.
 */
final class Constants
{
	/**
	 * Private constructor prevents instantiation.
	 *
	 * @throws UnsupportedOperationException because this class cannot be instantiated.
	 */
	private Constants()
	{
		throw new UnsupportedOperationException("Constants(): Cannot instantiate Constants"); //$NON-NLS-1$
	}

	/**
	 * Type: {@code String}
	 * <p>
	 * Maps to a {@code String} in the store-and-forward {@code Bundle}.
	 */
	protected static final String INTENT_EXTRA_TOGGLE = "name.boyle.chris.ptlp.TOGGLE"; //$NON-NLS-1$

}
