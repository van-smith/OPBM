Dim $ROOT_DIR = @ScriptDir & "\..\.."

#include <..\opbm\opbmCommon.au3>

Const $FILEIO_CREATE_FILES		= "Create 10GB in 1000 files"
Const $FILEIO_COPY_FILES		= "Copy 10GB in 1000 files"

; Global declaration for timing constants
Dim $gBaselineSize
$gBaselineSize = 2
Dim $gBaselines[ $gBaselineSize ][2]
$gBaselines[0][0] = $FILEIO_CREATE_FILES
$gBaselines[0][1] = $FILEIO_CREATE_FILES_SCORE
$gBaselines[1][0] = $FILEIO_COPY_FILES
$gBaselines[1][1] = $FILEIO_COPY_FILES_SCORE
