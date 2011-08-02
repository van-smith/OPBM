This project contains an application which generates a specified load
across all cores on a CPU.  It uses a loop-based threading model which
may be able to be improved upon, but generates a workload (on top of the
existing system workload) up to the amount specified by command line
parameters.

Syntax:
	cpuload % duration_in_ms
	
Example:
	cpuload 50 10000
	; Generate a 50% workload on all cores for 10 seconds

- Rick C. Hodgin
- August 2, 2011
