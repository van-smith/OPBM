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
Const $LAUNCH_ACROBAT_READER_SCORE 					= 1.093868293			;Updated 2011_12_13 -rcp 0.7268677485
Const $CLOSE_ACROBAT_READER_SCORE					= 0.85				; No longer recorded separately, but as part of Acrobat's manipulation in Alice Word
; acrobatReaderInstall.au3
Const $LAUNCH_ACROBAT_READER_INSTALLER_SCORE		= 8.802943002			;Updated 2011_12_13 -rcp 0.72686774858.3471664062
Const $INSTALL_ACROBAT_READER_SCORE					= 38.45374079			;Updated 2011_12_13 -rcp 0.726867748528.9721825692
; acrobatReaderUninstall.au3
Const $LAUNCH_ACROBAT_READER_UNINSTALLER_SCORE		= 3.292720468			;Updated 2011_12_13 -rcp 0.72686774853.2928334779
Const $UNINSTALL_ACROBAT_READER_SCORE				= 7.60787042			;Updated 2011_12_13 -rcp 0.72686774856.8550649203

; chromeCommon.au3
Const $LAUNCH_CHROME_SCORE							= 1.098161845			;Updated 2011_12_13 -rcp 0.5695902629
Const $CLOSE_CHROME_SCORE							= 0.636683525			;Updated 2011_12_13 -rcp 0.8638433956
; chromeGoogleV8.au3
Const $CHROME_TYPE_GOOGLEV8_URL_SCORE				= 3.411725693			;Updated 2011_12_13 -rcp 2.091220093
Const $CHROME_RUN_GOOGLEV8_SCORE					= 19.89621753			;Updated 2011_12_13 -rcp 19.7012510653
; chromeInstall.au3
Const $INSTALL_CHROME_SCORE							= 28.07729524			;Updated 2011_12_13 -rcp 10.4107596398
; chromeKraken.au3
Const $CHROME_TYPE_KRAKEN_URL_SCORE					= 3.443586263			;Updated 2011_12_13 -rcp 2.1411169829
Const $CHROME_RUN_KRAKEN_SCORE						= 37.90223702			;Updated 2011_12_13 -rcp 46.5021759834
; chromeSpider.au3
Const $CHROME_TYPE_SUNSPIDER_URL_SCORE				= 3.440448459			;Updated 2011_12_13 -rcp 2.1198768282
Const $CHROME_RUN_SUNSPIDER_SCORE					= 14.27906306			;Updated 2011_12_13 -rcp 13.5010165074
; chromeUninstall.au3
Const $LAUNCH_CHROME_UNINSTALLER_SCORE				= 1.092401531			;Updated 2011_12_13 -rcp 0.4682645513
Const $UNINSTALL_CHROME_SCORE						= 0.514046371			;Updated 2011_12_13 -rcp 1.3385230413

; firefoxCommon.au3
Const $LAUNCH_FIREFOX_SCORE							= 1.328500621			;Updated 2011_12_13 -rcp 0.9391000844
Const $CLOSE_FIREFOX_SCORE							= 0.941136787			;Updated 2011_12_13 -rcp 0.9265976358
; firefoxGoogleV8.au3
Const $FIREFOX_TYPE_GOOGLEV8_URL_SCORE				= 3.555826013			;Updated 2011_12_13 -rcp 2.2150851332
Const $FIREFOX_RUN_GOOGLEV8_SCORE					= 20.45178093			;Updated 2011_12_13 -rcp 20.4516685115
; firefoxInstall.au3
Const $LAUNCH_FIREFOX_INSTALLER_SCORE				= 2.584277671			;Updated 2011_12_13 -rcp 2.0017837012
Const $BYPASS_NEXT_BUTTON_SCORE						= 1.161080389			;Updated 2011_12_13 -rcp 0.9668963862
Const $INSTALL_FIREFOX_SCORE						= 1.798527871			;Updated 2011_12_13 -rcp 1.07430346
; firefoxKraken.au3
Const $FIREFOX_TYPE_KRAKEN_URL_SCORE				= 3.586730283			;Updated 2011_12_13 -rcp 2.2462705561
Const $FIREFOX_RUN_KRAKEN_SCORE						= 48.44303448			;Updated 2011_12_13 -rcp 41.979321204
; firefoxSpider.au3
Const $FIREFOX_TYPE_SUNSPIDER_URL_SCORE				= 3.623819108			;Updated 2011_12_13 -rcp 2.246297302
Const $FIREFOX_RUN_SUNSPIDER_SCORE					= 16.2763783			;Updated 2011_12_13 -rcp 16.2306413049
; firefoxUninstall.au3
Const $LAUNCH_FIREFOX_UNINSTALLER_SCORE				= 0.460106162			;Updated 2011_12_13 -rcp 0.4630977124
Const $UNINSTALL_FIREFOX_SCORE						= 0.589431637			;Updated 2011_12_13 -rcp 0.4651684589
Const $CLOSE_UNINSTALLER_SCORE						= 0.170305066			;Updated 2011_12_13 -rcp 0.1699579784

; ieCommon.au3
Const $LAUNCH_IE_SCORE								= 0.668423111			;Updated 2011_12_13 -rcp 0.6081852193
Const $CLOSE_IE_SCORE								= 0.109149389			;Updated 2011_12_13 -rcp 0.1091609217
; ieGoogleV8.au3
Const $IE_TYPE_GOOGLEV8_URL_SCORE					= 3.400274449			;Updated 2011_12_13 -rcp 2.0585101442
Const $IE_RUN_GOOGLEV8_SCORE						= 20.90946103			;Updated 2011_12_13 -rcp 20.8635996455
; ieKraken.au3
Const $IE_TYPE_KRAKEN_URL_SCORE						= 3.431499156			;Updated 2011_12_13 -rcp 2.0897428378
Const $IE_RUN_KRAKEN_SCORE							= 115.3549922			;Updated 2011_12_13 -rcp 113.8263685837
; ieSpider.au3
Const $IE_TYPE_SUNSPIDER_URL_SCORE					= 3.431495216			;Updated 2011_12_13 -rcp 2.089764483
Const $IE_RUN_SUNSPIDER_SCORE						= 10.52298752			;Updated 2011_12_13 -rcp 10.0216024462

; accessCommon.au3
Const $ACCESS_COPY_EARTHQUAKE_SCORE					= 0.872458391			;Updated 2011_12_13 -rcp 0.07						; placeholder
Const $LAUNCH_MICROSOFT_ACCESS_SCORE				= 13.55270638			;Updated 2011_12_13 -rcp 0.78			; placeholder
Const $CLOSE_MICROSOFT_ACCESS_SCORE					= 0.592662515			;Updated 2011_12_13 -rcp 0.70			; placeholder
Const $ACCESS_OPEN_EARTHQUAKE_SCORE					= 3.362621825			;Updated 2011_12_13 -rcp 3.32						; placeholder
Const $ACCESS_EARTHQUAKE_QUERIES_SCORE				= 25.80724789			;Updated 2011_12_13 -rcp 26.54						; placeholder
Const $ACCESS_EARTHQUAKE_REPORTS_SCORE				= 14.79864586			;Updated 2011_12_13 -rcp 14.83						; placeholder
Const $ACCESS_COMPACT_EARTHQUAKE_SCORE				= 1.537613504			;Updated 2011_12_13 -rcp 1.73						; placeholder

; excelCommon.au3
Const $LAUNCH_MICROSOFT_EXCEL_SCORE					= 0.5664435075
Const $CLOSE_MICROSOFT_EXCEL_SCORE					= 0.16

; powerpointCommon.au3
Const $LAUNCH_MICROSOFT_POWERPOINT_SCORE			= 16.62401949			;Updated 2011_12_13 -rcp 0.863637155266839			; placeholder
Const $CLOSE_MICROSOFT_POWERPOINT_SCORE				= 0.701865949			;Updated 2011_12_13 -rcp 0.902639997163701			; placeholder

; powerpointWar.au3
Const $WAR_OPEN_PRESENTATION_SCORE					= 2.194268269			;Updated 2011_12_13 -rcp 3.77			; placeholder
Const $WAR_PLAY_PRESENTATION_SCORE					= 90.6857959			;Updated 2011_12_13 -rcp 89			; placeholder
Const $WAR_CREATE_WMV_SCORE							= 88.07303597			;Updated 2011_12_13 -rcp 86			; placeholder

; publisherCommon.au3
Const $LAUNCH_MICROSOFT_PUBLISHER_SCORE				= 13.55133181			;Updated 2011_12_13 -rcp 0.82			; placeholder
Const $CLOSE_MICROSOFT_PUBLISHER_SCORE				= 1.440120181			;Updated 2011_12_13 -rcp 0.98			; placeholder

; publisherHedge.au3
Const $HEDGE_OPEN_FLYER_SCORE						= 4.563671325			;Updated 2011_12_13 -rcp 3.21			; placeholder
Const $HEDGE_PAGE_SCORE								= 4.086956675			;Updated 2011_12_13 -rcp 4.12						; placeholder
Const $HEDGE_ROTATE_SCORE							= 2.790896909			;Updated 2011_12_13 -rcp 2.75						; placeholder
Const $HEDGE_SAVE_XPS_SCORE							= 3.925325788			;Updated 2011_12_13 -rcp 3.9						; placeholder
Const $HEDGE_ZOOM_XPS_SCORE							= 8.875962755			;Updated 2011_12_13 -rcp 8.88						; placeholder
Const $HEDGE_XPS_EXIT_SCORE							= 1.029703179			;Updated 2011_12_13 -rcp 1.03						; placeholder

; wordCommon.au3
Const $LAUNCH_MICROSOFT_WORD_SCORE					= 13.44886024			;Updated 2011_12_13 -rcp 0.5535907745
Const $CLOSE_MICROSOFT_WORD_SCORE					= 0.61086455			;Updated 2011_12_13 -rcp 0.1542205868

; excel/heat.au3
Const $HEAT_CLOSE_MICROSOFT_EXCEL_SCORE				= 0.483117738			;Updated 2011_12_13 -rcp 0.4831443451
Const $HEAT_LAUNCH_MICROSOFT_EXCEL_SCORE			= 13.44886024			;Updated 2011_12_13 -rcp 0.5664435075
Const $HEAT_CLOSE_EMPTY_WORKSHEET_SCORE				= 0.509618117			;Updated 2011_12_13 -rcp 0.4991667927
Const $HEAT_OPEN_WORKSHEET_SCORE					= 5.796046392			;Updated 2011_12_13 -rcp 4.6579648657
Const $HEAT_SAVE_AND_CLOSE_WORKSHEET_SCORE			= 1.916449769			;Updated 2011_12_13 -rcp 1.9203279355
Const $HEAT_TIME_TO_ITERATE_N_TIMES_SCORE			= 28.17587902			;Updated 2011_12_13 -rcp 27.7082578299

; word/alice.au3
Const $ALICE_CLOSE_MICROSOFT_WORD_SCORE				= 1.201154619			;Updated 2011_12_13 -rcp 0.1542205868
Const $ALICE_LAUNCH_MICROSOFT_WORD_SCORE			= 13.44886024			;Updated 2011_12_13 -rcp 0.5535907745
Const $ALICE_CLOSE_EMPTY_DOCUMENT_SCORE				= 0.48				; No longer used since copy-and-paste-into-Word operation
Const $ALICE_OPEN_ALICE_IN_WONDERLAND_SCORE			= 4.357309037			;Updated 2011_12_13 -rcp 3.0789126871
Const $ALICE_TIME_TO_PAGE_DOWN_N_TIMES_SCORE		= 5.74				; No longer used since paging now takes place in IE and Word using normal text and font-effects
Const $ALICE_SAVE_AS_PDF_SCORE						= 6.033150792			;Updated 2011_12_13 -rcp 5.8838713978
Const $ALICE_MANIPULATE_IN_ACROBAT_READER_SCORE		= 37.13291033			;Updated 2011_12_13 -rcp 33.8675221741
Const $ALICE_PAGE_DOWN_N_TIMES_IN_IE9_SCORE			= 9.718513698			;Updated 2011_12_13 -rcp 9.1635933029
Const $ALICE_TIME_TO_PGDN_N_TIMES_NORMALLY_SCORE	= 24.47756745			;Updated 2011_12_13 -rcp 26.0228763641
Const $ALICE_TIME_TO_PGDN_N_TIMES_FONT_FX_SCORE		= 11.74989062			;Updated 2011_12_13 -rcp 14.7099573395
Const $ALICE_LAUNCH_IE_SCORE						= 0.637887661			;Updated 2011_12_13 -rcp 0.6068217634
Const $ALICE_CLOSE_IE_SCORE							= 0.90438495			;Updated 2011_12_13 -rcp 0.9042088168
Const $ALICE_COPY_TO_CLIPBOARD_SCORE				= 1.669011382			;Updated 2011_12_13 -rcp 1.6686911127
Const $ALICE_PASTE_INTO_DOCUMENT_SCORE				= 5.811861041			;Updated 2011_12_13 -rcp 5.4507718809
Const $ALICE_SET_FONT_LIGATURES_SCORE				= 2.01921731			;Updated 2011_12_13 -rcp 2.0434162523
Const $ALICE_UPDATING_IMAGE_TEXT_ALIGNMENT_SCORE	= 46.01911636			;Updated 2011_12_13 -rcp 47.838000219

; word/wordIsland.au3
Const $WORD_ISLAND_SCORE							= 30.90683965			;Updated 2011_12_13 -rcp 30.97		; placeholder
Const $WORD_ISLAND_SAVE_SCORE						= 3.073072112			;Updated 2011_12_13 -rcp 2.93		; placeholder

; operaCommon.au3
Const $LAUNCH_OPERA_SCORE							= 0.460209477			;Updated 2011_12_13 -rcp 1.2104668343
Const $CLOSE_OPERA_SCORE							= 1.177545436			;Updated 2011_12_13 -rcp 0.877898464
; operaGoogleV8.au3
Const $OPERA_TYPE_GOOGLEV8_URL_SCORE				= 3.507457563			;Updated 2011_12_13 -rcp 2.7365318898
Const $OPERA_RUN_GOOGLEV8_SCORE						= 20.6255986			;Updated 2011_12_13 -rcp 19.9121319855
; operaInstall.au3
Const $LAUNCH_OPERA_INSTALLER_SCORE					= 3.507783101			;Updated 2011_12_13 -rcp 3.3354794343	;Eliminate Version reference -rcp 11/11/11
Const $INSTALL_OPERA_SCORE							= 4.488536309			;Updated 2011_12_13 -rcp 4.0499516118	;Eliminate Version reference -rcp 11/11/11
; operaKraken.au3
Const $OPERA_TYPE_KRAKEN_URL_SCORE					= 3.532303913			;Updated 2011_12_13 -rcp 2.382583979
Const $OPERA_RUN_KRAKEN_SCORE						= 96.28129596			;Updated 2011_12_13 -rcp 94.0016729283
; operaSpider.au3
Const $OPERA_TYPE_SUNSPIDER_URL_SCORE				= 3.492745814			;Updated 2011_12_13 -rcp 2.3248779646
Const $OPERA_RUN_SUNSPIDER_SCORE					= 19.94721627			;Updated 2011_12_13 -rcp 14.1010537385
; operaUninstall.au3
Const $LAUNCH_OPERA_UNINSTALLER_SCORE				= 0.579729318			;Updated 2011_12_13 -rcp 0.577146593	;Eliminate Version reference -rcp 11/11/11
Const $UNINSTALL_OPERA_SCORE						= 1.385014004			;Updated 2011_12_13 -rcp 0.514981939	;Eliminate Version reference -rcp 11/11/11

; opbmInternal.au3 (used by rebootTime.au3, startupSettleDown.au3)
Const $STARTUP_SETTLE_DOWN_SCORE					= 60.0		; placeholder
;Const $REBOOT_TIME_SCORE							= ; not used
; The reboot time is recorded as an internal function of the harness
; It is scored there also.  See the HUD's stream gobbler's commands.
; And specifically see BenchmarkParams' getRebootTimeScore() method.

; safariCommon.au3
Const $LAUNCH_SAFARI_SCORE							= 0.751664311			;Updated 2011_12_13 -rcp 0.7298296366
Const $CLOSE_SAFARI_SCORE							= 0.880291113			;Updated 2011_12_13 -rcp 0.8986433763
; safariGoogleV8.au3
Const $SAFARI_TYPE_GOOGLEV8_URL_SCORE				= 3.489323834			;Updated 2011_12_13 -rcp 2.0885815708
Const $SAFARI_RUN_GOOGLEV8_SCORE					= 21.50432836			;Updated 2011_12_13 -rcp 21.5040893603
; safariInstall.au3
Const $LAUNCH_SAFARI_INSTALLER_SCORE				= 3.372351767			;Updated 2011_12_13 -rcp 3.2958200527
Const $INSTALL_SAFARI_SCORE							= 18.504819			;Updated 2011_12_13 -rcp 15.0757779403
; safariKraken.au3
Const $SAFARI_TYPE_KRAKEN_URL_SCORE					= 3.552497388			;Updated 2011_12_13 -rcp 2.1384073052
Const $SAFARI_RUN_KRAKEN_SCORE						= 106.4637989			;Updated 2011_12_13 -rcp 127.4175001287
; safariSpider.au3
Const $SAFARI_TYPE_SUNSPIDER_URL_SCORE				= 3.649848203			;Updated 2011_12_13 -rcp 2.1166738394
Const $SAFARI_RUN_SUNSPIDER_SCORE					= 15.06499906			;Updated 2011_12_13 -rcp 8.1006120967
; safariUninstall.au3
Const $SAFARI_LAUNCH_SAFARI_UNINSTALLER_SCORE		= 0.664505944			;Updated 2011_12_13 -rcp 0.4564898075
Const $SAFARI_UNINSTALL_SAFARI_SCORE				= 4.761962494			;Updated 2011_12_13 -rcp 3.2637693345


; 7zipInstall.au3
Const $LAUNCH_SEVENZIP_INSTALLER_SCORE				= 0.719283745			;Updated 2011_12_13 -rcp 0.6922401247		; Placeholder. Not officially assigned yet
Const $INSTALL_SEVENZIP_SCORE						= 14.38390925			;Updated 2011_12_13 -rcp 14.154999042		; Placeholder. Not officially assigned yet
; 7zipUninstall.au3
Const $LAUNCH_SEVENZIP_UNINSTALLER_SCORE			= 0.716897361			;Updated 2011_12_13 -rcp 0.4087737230		; Placeholder. Not officially assigned yet
Const $UNINSTALL_SEVENZIP_SCORE						= 3.33437495			;Updated 2011_12_13 -rcp 4.2507326948		; Placeholder. Not officially assigned yet
; 7zipRunTest.au3
;Const $SEVENZIP_CREATE_7Z_ARCHIVE_SCORE			= 55.1601422391581	; Placeholder. Not officially assigned yet
Const $SEVENZIP_CREATE_7Z_ARCHIVE_SCORE				= 47.17583476			;Updated 2011_12_13 -rcp 47.2510798406368	;Updated 2011_12_11 -rcp
;Const $SEVENZIP_CREATE_ZIP_ARCHIVE_SCORE			= 62.0753465813396	; Placeholder. Not officially assigned yet
Const $SEVENZIP_CREATE_ZIP_ARCHIVE_SCORE			= 115.1502787			;Updated 2011_12_13 -rcp 115.328927104886	;Updated 2011_12_11 -rcp
;Const $SEVENZIP_7Z_UNARCHIVE_FIVE_TIMES_SCORE		= 62.1698270806001	; Placeholder. Not officially assigned yet
Const $SEVENZIP_7Z_UNARCHIVE_FIVE_TIMES_SCORE		= 5.847975103			;Updated 2011_12_13 -rcp 5.91206755057729	;Updated 2011_12_11 -rcp
;Const $SEVENZIP_ZIP_UNARCHIVE_FIVE_TIMES_SCORE		= 21.850377531907	; Placeholder. Not officially assigned yet
Const $SEVENZIP_ZIP_UNARCHIVE_FIVE_TIMES_SCORE		= 5.721605058			;Updated 2011_12_13 -rcp 6.45850487705019	;Updated 2011_12_11 -rcp
;Const $SEVENZIP_TEST_7Z_ARCHIVE_INTEGRITY_SCORE	= 9.42108409452225	; Placeholder. Not officially assigned yet
Const $SEVENZIP_TEST_7Z_ARCHIVE_INTEGRITY_SCORE		= 1.091572939			;Updated 2011_12_13 -rcp 1.14342080572347	;Updated 2011_12_11 -rcp
;Const $SEVENZIP_TEST_ZIP_ARCHIVE_INTEGRITY_SCORE	= 1.10049678312566	; Placeholder. Not officially assigned yet
Const $SEVENZIP_TEST_ZIP_ARCHIVE_INTEGRITY_SCORE	= 0.939271253			;Updated 2011_12_13 -rcp 0.935839835503731	;Updated 2011_12_11 -rcp

; fileioCommon.au3
Const $FILEIO_CREATE_FILES_SCORE					= 135.6407395			;Updated 2011_12_13 -rcp 439.29954400524	; Placeholder. Not officially assigned yet
Const $FILEIO_COPY_FILES_SCORE						= 149.280586			;Updated 2011_12_13 -rcp 401.2560807856	; Placeholder. Not officially assigned yet
