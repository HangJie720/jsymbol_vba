Attribute VB_Name = "Form2_Com"
Dim ID() As String
Public Function Get_QQ_Name() '得到当前的群名和UIN
On Error Resume Next
    Dim ScriptObj As Object
    vfwebqq1 = GetIniFileString("QQ", "vfwebqq", "", 500, App.path & "\Config.ini") '得到vfwebqq
    hash = GetIniFileString("QQ", "hash", "", 500, App.path & "\Config.ini") '得到hash
    p_uin = GetIniFileString("QQ", "p_uin", "", 500, App.path & "\Config.ini") '得到hash
    p_skey = GetIniFileString("QQ", "pskey", "", 500, App.path & "\Config.ini") '得到hash
    pt4_token = GetIniFileString("QQ", "pt4_token", "", 500, App.path & "\Config.ini") '得到hash
    ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到hash

    If Len(Trim(setCookies)) = 0 Then setCookies = "a:x,"
    Set WinHttp = CreateObject("WinHttp.WinHttpRequest.5.1") '创建WinHttp.WinHttpRequest
    'MsgBox "到 这里"
    WinHttp.Open "POST", "http://s.web2.qq.com/api/get_group_name_list_mask2", True '同步接收数据
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
    get_jsonqun = WinHttp.ResponseText
    Set ScriptObj = CreateObject("MSScriptControl.ScriptControl")
    ScriptObj.AllowUI = True
    ScriptObj.Language = "JavaScript"
    ScriptObj.AddCode "var data = " & get_jsonqun & ";"
'立即窗口显示第一个delist_time
    For i = 0 To 200 '当前最多就是二百个群。
    'Form2.Text1.Text = Form2.Text1.Text + "群名称：|" & ScriptObj.Eval("data.result.gnamelist[" & i & "].name") & "|Gid:|" & ScriptObj.Eval("data.result.gnamelist[" & i & "].gid") & vbCrLf
    Form2.MSHFlexGrid2.TextMatrix(i + 1, 1) = ScriptObj.Eval("data.result.gnamelist[" & i & "].name")
    Form2.MSHFlexGrid2.RowHeight(i + 1) = 300
    '设置数组
    Form2.MSHFlexGrid2.TextMatrix(i + 1, 2) = ScriptObj.Eval("data.result.gnamelist[" & i & "].gid")
    Form2.MSHFlexGrid2.TextMatrix(i + 1, 3) = ScriptObj.Eval("data.result.gnamelist[" & i & "].code")
    SetIniFileString "Group", ScriptObj.Eval("data.result.gnamelist[" & i & "].gid"), ScriptObj.Eval("data.result.gnamelist[" & i & "].name"), App.path & "\Group.ini"
    Next
End Function


Public Function Post_QQ_Message(ByVal XmlHttpMode$, ByVal XmlHttpURL$, ByVal XmlHttpData$)
    Dim MyXmlhttp
    '得到各种参数，此处一定要小心
    vfwebqq1 = GetIniFileString("QQ", "vfwebqq", "", 500, App.path & "\Config.ini") '得到vfwebqq
    hash = GetIniFileString("QQ", "hash", "", 500, App.path & "\Config.ini") '得到hash
    p_uin = GetIniFileString("QQ", "p_uin", "", 500, App.path & "\Config.ini") '得到hash
    p_skey = GetIniFileString("QQ", "pskey", "", 500, App.path & "\Config.ini") '得到hash
    pt4_token = GetIniFileString("QQ", "pt4_token", "", 500, App.path & "\Config.ini") '得到hash
    ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到hash
    sKey = GetIniFileString("QQ", "skey", "", 500, App.path & "\Config.ini") '得到hash
    On Error GoTo wrong
    Set MyXmlhttp = CreateObject("WinHttp.WinHttpRequest.5.1")                  '创建WinHttpRequest对象
    With MyXmlhttp
        .setTimeouts 50000, 50000, 50000, 50000                                 '设置超时时间
        If XmlHttpMode = "GET" Then                                             '异步GET请求
            .Open "GET", XmlHttpURL, True
        Else
            .Open "POST", XmlHttpURL, True                                      '异步POST请求
            .SetRequestHeader "Content-Type", "application/x-www-form-urlencoded"
        End If
        .SetRequestHeader "Cookie", "uin=" & p_uin & ";skey=" & sKey & ";p_uin=" & p_uin & ";p_skey=" & p_skey & ";pt4_token=" & pt4_token & ";ptwebqq=" & ptwebqq
        .SetRequestHeader "referer", "https://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1"

        .Send (XmlHttpData)
        .WaitForResponse                                                        '异步等待
        If MyXmlhttp.Status = 200 Then                                          '成功获取页面
            Post_QQ_Message = StrConv(.ResponseBody, vbUnicode)
        Else
            MsgBox "Http错误代码:" & .Status, vbInformation, "提示"
        End If
    End With
    Set MyXmlhttp = Nothing
    Exit Function
wrong:
    MsgBox "错误原因:" & Err.Description & "", vbInformation, "提示"
    Set MyXmlhttp = Nothing
End Function
