package opbm.benchmarks.waituntilidle;

public class WaitUntilIdleParams
{
	public WaitUntilIdleParams(int	percentageDesired,
							 int	duration,
							 int	timeout)
	{
		m_percentageDesired		= percentageDesired;
		m_duration				= duration;
		m_timeout				= timeout;
		m_returnCode			= 0;
	}

	public	int		m_percentageDesired;
	public	int		m_duration;
	public	int		m_timeout;
	public	int		m_returnCode;
}
