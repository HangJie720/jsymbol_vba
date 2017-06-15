Attribute VB_Name = "Json"
'Json内容处理
Public Function Strjiexi(ByVal jiexi$) 'JSON解析psessionid
On Error Resume Next
Dim ScriptObj As Object
Set ScriptObj = CreateObject("MSScriptControl.ScriptControl")
ScriptObj.AllowUI = True
ScriptObj.Language = "JavaScript"
ScriptObj.AddCode "var data = " & jiexi & ";"
'立即窗口显示第一个delist_time
jiexi = ScriptObj.Eval("data.result.psessionid")
aa1 = SetIniFileString("QQ", "psessionid", jiexi, App.path & "\Config.ini")
Label2.Caption = "解析完毕"
End Function

Public Function JScript()   '得到正确的HASH并写入配置文件
On Error Resume Next
    Dim JScript1 As String
    Dim obj As Object
    code = Form1.Text2.Text
    Set obj = CreateObject("MSScriptControl.ScriptControl")
    obj.AllowUI = True
    obj.Language = "JavaScript"
    JScript1 = obj.Eval(code)
    aa2 = SetIniFileString("QQ", "hash", JScript1, App.path & "\Config.ini")
End Function

Public Function strvfwebqq(ByVal jiexi$) 'JSON解析vfwebqq
On Error Resume Next
Dim ScriptObj As Object
Set ScriptObj = CreateObject("MSScriptControl.ScriptControl")
ScriptObj.AllowUI = True
ScriptObj.Language = "JavaScript"
ScriptObj.AddCode "var data = " & jiexi & ";"
'立即窗口显示第一个delist_time
jiexi = ScriptObj.Eval("data.result.vfwebqq")
aa1 = SetIniFileString("QQ", "vfwebqq", jiexi, App.path & "\Config.ini")
End Function

Public Function Message_Json(ByVal jiexi$) 'JSON解析收到的消息
On Error Resume Next
Dim jiexi1 As String
Dim ScriptObj As Object
Set ScriptObj = CreateObject("MSScriptControl.ScriptControl")
ScriptObj.AllowUI = True
ScriptObj.Language = "JavaScript"
ScriptObj.AddCode "var data = " & jiexi & ";"
'立即窗口显示第一个delist_time
jiexi = ScriptObj.Eval("data.result[0].value.content[1]")
jiexi1 = ScriptObj.Eval("data.result[0].value.group_code")
QQ_Group_Name = GetIniFileString("Group", jiexi1, "", 500, App.path & "\Group.ini") '得到hash
Form2.List1.AddItem "Name：" & QQ_Group_Name & "|Content:" & jiexi
End Function
