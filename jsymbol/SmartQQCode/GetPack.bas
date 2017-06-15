Attribute VB_Name = "GetPack"
Public Function ReadinteFile(ByVal sUrl As String) As String  '此处是得第一次登陆信息的源码
Dim xmlHTTP1 As Object
Set xmlHTTP1 = CreateObject("Microsoft.XMLHTTP")
xmlHTTP1.Open "get", sUrl, True
xmlHTTP1.Send
While xmlHTTP1.ReadyState <> 4
DoEvents
Wend
ReadinteFile = xmlHTTP1.ResponseText
Set xmlHTTP1 = Nothing
End Function


Public Function Getsky(ByVal XmlHttpData$) '此处是写二次登陆的参数
On Error Resume Next
Dim strData As String
    If Len(Trim(setCookies)) = 0 Then setCookies = ""
    Set WinHttp = CreateObject("WinHttp.WinHttpRequest.5.1") '创建WinHttp.WinHttpRequest
    'MsgBox "到 这里"
    WinHttp.setTimeouts 50000, 50000, 50000, 50000
    WinHttp.Open "GET", XmlHttpData, True '同步接收数据
    WinHttp.Option(6) = False
     '其它请求头设置
    WinHttp.SetRequestHeader "Connection", "Close"
    WinHttp.SetRequestHeader "Content-Type", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
    WinHttp.Send
    WinHttp.WaitForResponse '等待请求
    'MsgBox
    WinHttp.Status '请求状态
    '得到返回文本(或者是其它)
headers = WinHttp.getAllResponseHeaders
'提取Psky
strData = Split(Split(headers, "p_skey=")(1), ";")(0)
SetIniFileString "QQ", "pskey", strData, App.path & "\Config.ini"
strData = Split(Split(headers, "uin=")(1), ";")(0)
SetIniFileString "QQ", "uin", strData, App.path & "\Config.ini"
strData = Split(Split(headers, "skey=")(1), ";")(0)
SetIniFileString "QQ", "skey", strData, App.path & "\Config.ini"
strData = Split(Split(headers, "p_uin=")(1), ";")(0)
SetIniFileString "QQ", "p_uin", strData, App.path & "\Config.ini"
strData = Split(Split(headers, "pt4_token=")(1), ";")(0)
SetIniFileString "QQ", "pt4_token", strData, App.path & "\Config.ini"
Form1.Label2.Caption = "各参数写入完毕。"
End Function

Public Function Get_vfwebqq(ByVal url$)
ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到ptqrtoken
pskey = GetIniFileString("QQ", "pskey", "", 500, App.path & "\Config.ini") '得到pskey
pt4_token = GetIniFileString("QQ", "pt4_token", "", 500, App.path & "\Config.ini") '得到pskey
p_uin = GetIniFileString("QQ", "p_uin", "", 500, App.path & "\Config.ini") '得到pskey
sKey = GetIniFileString("QQ", "skey", "", 500, App.path & "\Config.ini") '得到pskey
uin = GetIniFileString("QQ", "uin", "", 500, App.path & "\Config.ini") '得到uin
Set request = CreateObject("WinHttp.WinHttpRequest.5.1") '根据对象签名创建对象
request.setTimeouts 50000, 50000, 50000, 50000
request.Option(4) = 13056
request.Option(6) = False
request.Open "GET", url, True, "", ""
request.SetRequestHeader "Referer", "http://s.web2.qq.com/proxy.html?v=20" '这个必须要加上来路，不然会出错
request.SetRequestHeader "Cookie", "uin=" & uin & "; skey=" & sKey & "; p_uin=" & p_uin & "; p_skey=" & pskey & "; pt4_token=" & pt4_token & "; ptwebqq=" & ptwebqq '还必须加上COOKIE，不然得不到信息
request.Send
request.WaitForResponse
If request.Status = 200 Then
strvfwebqq (request.ResponseText)
Label2.Caption = "正在得到vfwebqq参数"
End If
End Function
