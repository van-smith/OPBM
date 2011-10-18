; opbm\
DirRemove( "opbm\ipch\", 1 )
DirRemove( "opbm\Debug\", 1)
DirRemove( "opbm\Release\", 1)
DirRemove( "opbm\x64\", 1)
FileDelete( "opbm\opbm.sdf" )
FileDelete( "opbm\opbm2.sdf" )

; opbm\benchmark
DirRemove( "opbm\benchmark\Debug\", 1)
DirRemove( "opbm\benchmark\Release\", 1)
DirRemove( "opbm\benchmark\x64\", 1)

; opbm\cpu
DirRemove( "opbm\cpu\Debug\", 1)
DirRemove( "opbm\cpu\Release\", 1)
DirRemove( "opbm\cpu\x64\", 1)

; opbm\enumthreads
DirRemove( "opbm\enumthreads\Debug\", 1)
DirRemove( "opbm\enumthreads\Release\", 1)
DirRemove( "opbm\enumthreads\x64\", 1)
DirRemove( "opbm\enumthreads\ipch\", 1)
FileDelete( "opbm\enumthreads\enumthreads.sdf" )

; opbm\jbm
DirRemove( "opbm\jbm\Debug\", 1)
DirRemove( "opbm\jbm\Release\", 1)
;DirRemove( "opbm\jbm\x64\", 1)

; AutoIt opbmCommon plugin opbm.dll:
; opbm\opbm
DirRemove( "opbm\opbm\Debug\", 1)
DirRemove( "opbm\opbm\Release\", 1)
DirRemove( "opbm\opbm\x64\", 1)

; opbm.jar JNI interface, opbm32.dll and opobm64.dll:
; opbm\opbm64
DirRemove( "opbm\opbm64\Debug\", 1)
DirRemove( "opbm\opbm64\Release\", 1)
DirRemove( "opbm\opbm64\x64\", 1)

; opbm\restarter
DirRemove( "opbm\restarter\Debug\", 1)
DirRemove( "opbm\restarter\Release\", 1)
DirRemove( "opbm\restarter\x64\", 1)

; opbm\spinup
DirRemove( "opbm\spinup\Debug\", 1)
DirRemove( "opbm\spinup\Release\", 1)
DirRemove( "opbm\spinup\x64\", 1)

; opbm\watchdog
DirRemove( "opbm\watchdog\Debug\", 1)
DirRemove( "opbm\watchdog\Release\", 1)
DirRemove( "opbm\watchdog\x64\", 1)
FileDelete( "opbm\watchdog\watchdog.sdf" )
