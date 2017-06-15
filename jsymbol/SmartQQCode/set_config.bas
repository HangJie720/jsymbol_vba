Attribute VB_Name = "set_config"
Option Explicit
Public Declare Function JToolsSetPrivateProfileString Lib "kernel32" Alias "WritePrivateProfileStringA" (ByVal lpApplicationName As String, ByVal lpKeyName As Any, ByVal lpString As Any, ByVal lpFileName As String) As Long
Public Declare Function JToolsGetPrivateProfileString Lib "kernel32" Alias "GetPrivateProfileStringA" (ByVal lpApplicationName As String, ByVal lpKeyName As Any, ByVal lpDefault As String, ByVal lpReturnedString As String, ByVal nSize As Long, ByVal lpFileName As String) As Long

'-----------ini修正-------------
Public Function SetIniFileString(sSection As String, sKey As String, sSetString As String, sFileName As String) As Long
    SetIniFileString = JToolsSetPrivateProfileString(sSection, sKey, sSetString, sFileName)
End Function
'-----------ini修正-------------

'-----------ini讀取-------------
Public Function GetIniFileString(sSection As String, sKey As String, sDefault As String, lSize As Long, sFileName As String) As String
Dim llLen As Long
Dim lsReturn As String
    lsReturn = Space(lSize + 1)
    llLen = JToolsGetPrivateProfileString(sSection, sKey, sDefault, lsReturn, lSize + 1, sFileName)
    GetIniFileString = MidTermB(lsReturn, 1, llLen)
End Function
'-----------ini讀取-------------

Public Function MidTermB(sIn As String, Optional lStart, Optional lLen, Optional iFlgSpace, Optional iFlgBinary) As String
    If IsMissing(iFlgBinary) Then
        MidTermB = MidTermB_String(sIn, lStart, lLen, iFlgSpace)
    Else
        MidTermB = MidTermB_Binary(sIn, lStart, lLen, iFlgSpace)
    End If
End Function



Public Function MidTermB_String(sIn As String, Optional lStart, Optional lLen, Optional iFlgSpace) As String
Dim lsResult As String
Dim lsTemp As String
Dim lsTemp2 As String
Dim liStartSpace As Integer
Dim i As Integer
Dim j As Integer
    On Error GoTo ERR_MidTermB_String
    lsResult = ""
    If IsMissing(lStart) Then lStart = 1
    If IsMissing(lLen) Then lLen = LenB(StrConv(sIn, vbFromUnicode)) - lStart + 1
    If IsMissing(iFlgSpace) Then iFlgSpace = 0 Else iFlgSpace = 1
    '初期位置の文字が２バイト系文字の２バイト目に無いかを判断します。
    lsTemp2 = ""
    For i = 1 To Len(sIn)
        lsTemp = StrConv(Mid(sIn, i, 1), vbFromUnicode)
        If LenB(lsTemp2) + LenB(lsTemp) > (lStart - 1) Then
            Exit For
        End If
        lsTemp2 = lsTemp2 + lsTemp
    Next i
    liStartSpace = 0
    If LenB(lsTemp2) < (lStart - 1) Then
        '２バイト系の２バイト目なので、返す文字列の１バイト目はスペースにします。
        lsResult = StrConv(" ", vbFromUnicode)
        liStartSpace = 1
    End If
    '指定した文字列の取得を行います。
    lsTemp2 = ""
    For j = i + liStartSpace To Len(sIn)
        lsTemp = StrConv(Mid(sIn, j, 1), vbFromUnicode)
        If LenB(lsTemp2) + LenB(lsTemp) > (lLen - liStartSpace) Then
            Exit For
        Else
            lsTemp2 = lsTemp2 + lsTemp
        End If
    Next j
    'スペースの付加を行います。
    If iFlgSpace Then
        lsResult = lsResult + LeftB(lsTemp2 + StrConv(String(lLen, " "), vbFromUnicode), lLen - liStartSpace)
    Else
        lsResult = lsResult + LeftB(lsTemp2, lLen - liStartSpace)
    End If
    MidTermB_String = StrConv(lsResult, vbUnicode)
    Exit Function
ERR_MidTermB_String:
    MidTermB_String = ""
End Function

Public Function MidTermB_Binary(sIn As String, Optional lStart, Optional lLen, Optional iFlgSpace) As String
Dim lsWEdit As String
Dim lsWOut As String
    On Error GoTo Exit_MidTermB_Binary
    lsWEdit = StrConv(sIn, vbFromUnicode)
    If IsMissing(lStart) Then lStart = 1
    If IsMissing(lLen) Then lLen = LenB(lsWEdit) - (lStart - 1)
    If IsMissing(iFlgSpace) Then iFlgSpace = 0
    lsWOut = MidB(lsWEdit, lStart, lLen)
    If iFlgSpace Then
        If LenB(lsWOut) < lLen Then
            lsWOut = lsWOut + StrConv(String((lLen - LenB(lsWOut)), " "), vbFromUnicode)
        End If
    End If
    MidTermB_Binary = StrConv(lsWOut, vbUnicode)
    Exit Function
Exit_MidTermB_Binary:
    MidTermB_Binary = ""
End Function






