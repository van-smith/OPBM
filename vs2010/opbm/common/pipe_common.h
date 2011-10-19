//////////
//
// pipe_common.h
//
/////
//
// Contains common structures used for named pipe data exchange.
//
//




//////////
// Each holds a name, and its length
// 32 characters
/////
	struct SPipeDataName
	{
		int			length;								// Length of the text stored in name
		char		name[32];							// Text of the name
	};

//////////
// 128 characters
/////
	struct SPipeDataLongName
	{
		int			length;								// Length of the text stored in name
		char		name[128];							// Text of the name
	};

//////////
// Filename
/////
	struct SPipeDataFilename
	{
		int			length;								// Length of the text stored in name
		char		filename[_MAX_FNAME];				// Text of the filename
	};

//////////
// Directory
/////
	struct SPipeDataDirectory
	{
		int			length;								// Length of the text stored in name
		char		directory[_MAX_PATH];				// Text of the directory
	};

//////////
// Registry Keys
/////
	struct SPipeDataRegistryKeyName
	{
		int			length;								// Length of the key name
		char		key[1024];							// Text of the key
	};

	struct SPipeDataRegistryKeyValue
	{
		int			length;								// Length of the key value
		char		value[1024];						// Text of the key value
	};
