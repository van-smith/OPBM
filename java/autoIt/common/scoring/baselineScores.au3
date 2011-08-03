; Scoring used by Opbm's AutoIt scripts.
; Refer to the gBaselines variable used throughout for timing events.
; Each gBaselines entry has a name and a score, in the form:
;
;	gBaselines[n][0] = "some name"
;	gBaselines[n][1] = 0.67
;
; Which, following the naming protocol used in Opbm's AutoIt scripts, becomes:
;	gBaselines[n][0] = $SOME_NAME
;	gBaselines[n][1] = $SOME_NAME_SCORE
; Note:  The $SOME_NAME_SCORE are the Const entries contained in this file
;
; And if there is ambiguity in the name used by multiple separate .au3 files,
; then it would be resolved using a greater naming scope, such as:
;	gBaselines[n][0] = $APP1_SOME_NAME
;	gBaselines[n][1] = $APP1_SOME_NAME_SCORE
;	gBaselines[n][0] = $APP2_SOME_NAME
;	gBaselines[n][1] = $APP2_SOME_NAME_SCORE
;
; Or something similar.
;

; adobeCommon.au3
Const $LAUNCH_ACROBAT_READER_SCORE 					= 0.67
Const $CLOSE_ACROBAT_READER_SCORE					= 0.85
; acrobatReaderInstall.au3
Const $LAUNCH_ACROBAT_READER_INSTALLER_SCORE		= 6.85
Const $INSTALL_ACROBAT_READER_SCORE					= 27.67
; acrobatReaderUninstall.au3
Const $LAUNCH_ACROBAT_READER_UNINSTALLER_SCORE		= 3.29
Const $UNINSTALL_ACROBAT_READER_SCORE				= 8.35

; chromeCommon.au3
Const $LAUNCH_CHROME_SCORE							= 0.57
Const $CLOSE_CHROME_SCORE							= 0.86
; chromeGoogleV8.au3
Const $CHROME_TYPE_GOOGLEV8_URL_SCORE				= 2.11
Const $CHROME_RUN_GOOGLEV8_SCORE					= 20.00
; chromeInstall.au3
Const $INSTALL_CHROME_SCORE							= 10.02
; chromeKraken.au3
Const $CHROME_TYPE_KRAKEN_URL_SCORE					= 2.14
Const $CHROME_RUN_KRAKEN_SCORE						= 189.36
; chromeSpider.au3
Const $CHROME_TYPE_SUNSPIDER_URL_SCORE				= 2.14
Const $CHROME_RUN_SUNSPIDER_SCORE					= 14.00
; chromeUninstall.au3
Const $LAUNCH_CHROME_UNINSTALLER_SCORE				= 0.83
Const $UNINSTALL_CHROME_SCORE						= 1.03

; firefoxCommon.au3
Const $LAUNCH_FIREFOX_SCORE							= 0.83
Const $CLOSE_FIREFOX_SCORE							= 1.12
; firefoxGoogleV8.au3
Const $FIREFOX_TYPE_GOOGLEV8_URL_SCORE				= 2.23
Const $FIREFOX_RUN_GOOGLEV8_SCORE					= 20.45
; firefoxInstall.au3
Const $LAUNCH_FIREFOX_INSTALLER_SCORE				= 1.92
Const $BYPASS_NEXT_BUTTON_SCORE						= 0.97
Const $INSTALL_FIREFOX_SCORE						= 1.00
; firefoxKraken.au3
Const $FIREFOX_TYPE_KRAKEN_URL_SCORE				= 2.26
Const $FIREFOX_RUN_KRAKEN_SCORE						= 47.99
; firefoxSpider.au3
Const $FIREFOX_TYPE_SUNSPIDER_URL_SCORE				= 2.26
Const $FIREFOX_RUN_SUNSPIDER_SCORE					= 14.27
; firefoxUninstall.au3
Const $LAUNCH_FIREFOX_UNINSTALLER_SCORE				= 0.50
Const $UNINSTALL_FIREFOX_SCORE						= 0.51
Const $CLOSE_UNINSTALLER_SCORE						= 0.17

; ieCommon.au3
Const $LAUNCH_IE_SCORE								= 0.61
Const $CLOSE_IE_SCORE								= 0.11
; ieGoogleV8.au3
Const $IE_TYPE_GOOGLEV8_URL_SCORE					= 2.08
Const $IE_RUN_GOOGLEV8_SCORE						= 13.76
; ieKraken.au3
Const $IE_TYPE_KRAKEN_URL_SCORE						= 2.11
Const $IE_RUN_KRAKEN_SCORE							= 115.00
; ieSpider.au3
Const $IE_TYPE_SUNSPIDER_URL_SCORE					= 2.11
Const $IE_RUN_SUNSPIDER_SCORE						= 13.03

; excelCommon.au3
Const $LAUNCH_MICROSOFT_EXCEL_SCORE					= 0.50
Const $CLOSE_MICROSOFT_EXCEL_SCORE					= 0.16

; wordCommon.au3
Const $LAUNCH_MICROSOFT_WORD_SCORE					= 0.50
Const $CLOSE_MICROSOFT_WORD_SCORE					= 0.16

; excel/heat.au3
Const $HEAT_CLOSE_MICROSOFT_EXCEL					= 0.48
Const $HEAT_LAUNCH_MICROSOFT_EXCEL					= 1.91
Const $HEAT_CLOSE_EMPTY_WORKSHEET_SCORE				= 0.51
Const $HEAT_OPEN_WORKSHEET_SCORE					= 5.04
Const $HEAT_SAVE_AND_CLOSE_WORKSHEET_SCORE			= 1.92
Const $HEAT_TIME_TO_ITERATE_N_TIMES_SCORE			= 20.5

; word/alice.au3
Const $ALICE_CLOSE_EMPTY_DOCUMENT_SCORE				= 0.48
Const $ALICE_OPEN_ALICE_IN_WONDERLAND_SCORE			= 4.41
Const $ALICE_TIME_TO_PAGE_DOWN_N_TIMES_SCORE		= 5.74
Const $ALICE_SAVE_AS_PDF_SCORE						= 3.81
Const $ALICE_MANIPULATE_IN_ACROBAT_READER_SCORE		= 24.000000000000000	; No official score yet

; operaCommon.au3
Const $LAUNCH_OPERA_SCORE							= 1.21
Const $CLOSE_OPERA_SCORE							= 0.89
; operaGoogleV8.au3
Const $OPERA_TYPE_GOOGLEV8_URL_SCORE				= 2.39
Const $OPERA_RUN_GOOGLEV8_SCORE						= 20.51
; operaInstall.au3
Const $LAUNCH_OPERA_1150_INSTALLER_SCORE			= 3.29
Const $INSTALL_OPERA_1150_SCORE						= 4.04
; operaKraken.au3
Const $OPERA_TYPE_KRAKEN_URL_SCORE					= 2.43
Const $OPERA_RUN_KRAKEN_SCORE						= 93.75
; operaSpider.au3
Const $OPERA_TYPE_SUNSPIDER_URL_SCORE				= 2.41
Const $OPERA_RUN_SUNSPIDER_SCORE					= 14.51
; operaUninstall.au3
Const $LAUNCH_OPERA_1150_UNINSTALLER_SCORE			= 0.57
Const $UNINSTALL_OPERA_1150_SCORE					= 0.51

; safariCommon.au3
Const $LAUNCH_SAFARI_SCORE							= 0.67
Const $CLOSE_SAFARI_SCORE							= 0.89
; safariGoogleV8.au3
Const $SAFARI_TYPE_GOOGLEV8_URL_SCORE				= 2.11
Const $SAFARI_RUN_GOOGLEV8_SCORE					= 21.50
; safariInstall.au3
Const $LAUNCH_SAFARI_INSTALLER_SCORE				= 3.31
Const $INSTALL_SAFARI_SCORE							= 16.21
; safariKraken.au3
Const $SAFARI_TYPE_KRAKEN_URL_SCORE					= 2.14
Const $SAFARI_RUN_KRAKEN_SCORE						= 127.53
; safariSpider.au3
Const $SAFARI_TYPE_SUNSPIDER_URL_SCORE				= 2.14
Const $SAFARI_RUN_SUNSPIDER_SCORE					= 12.75
; safariUninstall.au3
Const $SAFARI_LAUNCH_SAFARI_UNINSTALLER_SCORE		= 0.91
Const $SAFARI_UNINSTALL_SAFARI_SCORE				= 3.82
