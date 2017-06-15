Attribute VB_Name = "Script_Handle"
Public Function Script(ByVal code$)
    ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 1000, App.path & "\Config.ini") 'µÃµ½vfwebqq
    Dim obj As Object
    code = "function hash(t){for (var e = 0, i = 0, n = t.length; n > i; ++i)e += (e << 5) +t.charCodeAt(i);return 2147483647 & e}hash(" & """" & ptwebqq & """" & ")"
    Set obj = CreateObject("MSScriptControl.ScriptControl")
    obj.AllowUI = True
    obj.Language = "JavaScript"
    Script = obj.Eval(code)
End Function
