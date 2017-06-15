Attribute VB_Name = "PostPack"
Public Function Postlogin(ByVal url$, ByVal neirong)
ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到ptqrtoken
pskey = GetIniFileString("QQ", "pskey", "", 500, App.path & "\Config.ini") '得到pskey
pt4_token = GetIniFileString("QQ", "pt4_token", "", 500, App.path & "\Config.ini") '得到pskey
p_uin = GetIniFileString("QQ", "p_uin", "", 500, App.path & "\Config.ini") '得到pskey
sKey = GetIniFileString("QQ", "skey", "", 500, App.path & "\Config.ini") '得到pskey
uin = GetIniFileString("QQ", "uin", "", 500, App.path & "\Config.ini") '得到uin
Set request = CreateObject("WinHttp.WinHttpRequest.5.1") '根据对象签名创建对象
request.setTimeouts 50000, 50000, 50000, 50000
request.Option(4) = 13056

request.Open "POST", url, True, "", ""
request.SetRequestHeader "Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2" '这个必须要加上来路，不然会出错
request.SetRequestHeader "Cookie", "uin=" & uin & "; skey=" & sKey & "; p_uin=" & p_uin & "; p_skey=" & pskey & "; pt4_token=" & pt4_token & "; ptwebqq=" & ptwebqq '还必须加上COOKIE，不然得不到信息
request.Send (neirong)
request.WaitForResponse
If request.Status = 200 Then
Strjiexi (request.ResponseText)
Label2.Caption = "正在尝试二次登陆"
End If
End Function

Public Function Post_info() As String '此函数是得到群所有的好友
    vfwebqq1 = GetIniFileString("QQ", "vfwebqq", "", 500, App.path & "\Config.ini") '得到vfwebqq
    hash = GetIniFileString("QQ", "hash", "", 500, App.path & "\Config.ini") '得到hash
    p_uin = GetIniFileString("QQ", "p_uin", "", 500, App.path & "\Config.ini") '得到hash
    p_skey = GetIniFileString("QQ", "pskey", "", 500, App.path & "\Config.ini") '得到hash
    pt4_token = GetIniFileString("QQ", "pt4_token", "", 500, App.path & "\Config.ini") '得到hash
    ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到hash
    If Len(Trim(setCookies)) = 0 Then setCookies = "a:x,"
    Set WinHttp = CreateObject("WinHttp.WinHttpRequest.5.1") '创建WinHttp.WinHttpRequest
    'MsgBox "到 这里"
    WinHttp.Open "POST", "http://s.web2.qq.com/api/get_user_friends2", True '同步接收数据
    WinHttp.Option(WinHttpRequestOption_SslErrorIgnoreFlags) = &H3300 '非常重要(忽略错误)
     '其它请求头设置
     
       
    WinHttp.SetRequestHeader "Content-Type", "application/x-www-form-urlencoded"
    WinHttp.SetRequestHeader "Cookie", "p_uin=" & p_uin & ";p_skey=" & p_skey & ";pt4_token=" & pt4_token & ";ptwebqq=" & ptwebqq
    WinHttp.SetRequestHeader "Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1"
    WinHttp.SetRequestHeader "Content-Length", Len("r={""vfwebqq"":""" & vfwebqq1 & """,""hash"":""" & hash & """}")
    WinHttp.Send "r={""vfwebqq"":""" & vfwebqq1 & """,""hash"":""" & hash & """}"
    WinHttp.WaitForResponse '等待请求
    'MsgBox WinHttp.Status'请求状态
    '得到返回文本(或者是其它)
     Form1.Text3.Text = WinHttp.ResponseText '进行JSON 对这个变量
       Label2.Caption = "用户信息已得到"
End Function

