const HKEY_LOCAL_MACHINE = &H80000002
strComputer = "."

dim Vistaver, dwvalue, UACStatus, UACValue

Set objWMIService = GetObject("winmgmts:\\" & strComputer & "\root\cimv2")

Set colItems = objWMIService.ExecQuery("Select * From Win32_OperatingSystem")

For Each objItem in colItems
    If Instr(objItem.Caption, "Vista") Then
        Select Case objItem.OperatingSystemSKU
             Case 0 strVersion = "Undefined"
             Case 1 strVersion = "Ultimate Edition" 
             Case 2 strVersion = "Home Basic Edition" 
             Case 3 strVersion = "Home Premium Edition" 
             Case 4 strVersion = "Enterprise Edition" 
             Case 5 strVersion = "Home Basic N Edition" 
             Case 6 strVersion = "Business Edition" 
             Case 7 strVersion = "Standard Server Edition" 
             Case 8 strVersion = "Datacenter Server Edition" 
             Case 9 strVersion = "Small Business Server Edition" 
             Case 10 strVersion = "Enterprise Server Edition" 
             Case 11 strVersion = "Starter Edition" 
             Case 12 strVersion = "Datacenter Server Core Edition" 
             Case 13 strVersion = "Standard Server Core Edition" 
             Case 14 strVersion = "Enterprise Server Core Edition" 
             Case 15 strVersion = "Enterprise Server IA64 Edition" 
             Case 16 strVersion = "Business N Edition" 
             Case 17 strVersion = "Web Server Edition" 
             Case 18 strVersion = "Cluster Server Edition" 
             Case 19 strVersion = "Home Server Edition" 
             Case 20 strVersion = "Storage Express Server Edition" 
             Case 21 strVersion = "Storage Standard Server Edition" 
             Case 22 strVersion = "Storage Workgroup Server Edition" 
             Case 23 strVersion = "Storage Enterprise Server Edition" 
             Case 24 strVersion = "Server For Small Business Edition" 
             Case 25 strVersion = "Small Business Server Premium Edition"
        End Select
	
	getUACStatus()
	if (UACValue=1) then
	
	sysINFO = msgbox ("Operating system : Windows Vista " & strVersion & vbcRLF & _
		"UAC (User Account Control) status : " & UACStatus & vbCRLF & vbCRLF &_
		"You have to disable UAC to continue. Do you want to disable it now?" & vbCRLF & vbCRLF &_
		"NOTE: Rerun this benchmark script after restart", 4 + 64, "x264 Benchmark HD")
		
		if (sysINFO=6) then
			invokeUAC()
		else
			msgbox "UAC is not disabled. Benchmark aborted. ", 64, "x264 Benchmark HD"
		end if
	else
		x264()

	end if
    Else
        x264()
    End If
Next  


'Get UAC status [Disabled or Enabled]
Function getUACStatus()
Set oReg=GetObject("winmgmts:{impersonationLevel=impersonate}!\\" &_
 strComputer & "\root\default:StdRegProv")
 
strKeyPath = "Software\Microsoft\Windows\CurrentVersion\Policies\System"
strValueName = "EnableLUA"
oReg.GetDWORDValue HKEY_LOCAL_MACHINE,strKeyPath,strValueName,dwValue

if (dwValue=1) then
UACStatus = "[Enabled]"
UACValue = dwvalue
else
UACStatus = "[Disabled]"
UACValue = dwvalue
end if
End function


'Invoke UAC
Function InvokeUAC()
set objshell = createobject("wscript.shell")
ireturn = objshell.run("c:\windows\system32\control.exe /name Microsoft.UserAccounts", 1, true)
End Function



'Run x264 Benchmark
Function x264()
set objshell = createobject("wscript.shell")
ireturn = objshell.run("cmd /c bench_script.bat")
end function
