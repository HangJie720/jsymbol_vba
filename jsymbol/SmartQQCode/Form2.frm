VERSION 5.00
Object = "{0ECD9B60-23AA-11D0-B351-00A0C9055D8E}#6.0#0"; "MSHFLXGD.OCX"
Begin VB.Form Form2 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "SmartQQ"
   ClientHeight    =   4650
   ClientLeft      =   45
   ClientTop       =   375
   ClientWidth     =   5775
   Icon            =   "Form2.frx":0000
   LinkTopic       =   "Form2"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   4650
   ScaleWidth      =   5775
   StartUpPosition =   2  '屏幕中心
   Begin VB.CommandButton Command4 
      Caption         =   "QQ群发器"
      Height          =   495
      Left            =   1680
      TabIndex        =   9
      Top             =   120
      Width           =   1455
   End
   Begin VB.Timer Timer1 
      Enabled         =   0   'False
      Interval        =   1000
      Left            =   120
      Top             =   2040
   End
   Begin VB.Frame Frame1 
      Caption         =   "接收QQ群消息"
      Height          =   1695
      Left            =   120
      TabIndex        =   7
      Top             =   2880
      Width           =   5535
      Begin VB.ListBox List1 
         Height          =   1320
         Left            =   120
         TabIndex        =   8
         Top             =   240
         Width           =   5295
      End
   End
   Begin VB.CommandButton Command1 
      Caption         =   "获取当前群"
      Height          =   495
      Left            =   120
      TabIndex        =   5
      Top             =   120
      Width           =   1455
   End
   Begin VB.TextBox Text2 
      Height          =   495
      Left            =   1080
      TabIndex        =   4
      Top             =   2280
      Width           =   2175
   End
   Begin VB.TextBox Text3 
      Height          =   495
      Left            =   4560
      TabIndex        =   3
      Text            =   "Gid"
      Top             =   2280
      Width           =   1095
   End
   Begin VB.CommandButton Command2 
      Caption         =   "发送"
      Height          =   495
      Left            =   3360
      TabIndex        =   2
      Top             =   2280
      Width           =   1095
   End
   Begin VB.CommandButton Command3 
      Caption         =   "帮助说明"
      Height          =   495
      Left            =   4200
      TabIndex        =   1
      Top             =   120
      Width           =   1455
   End
   Begin MSHierarchicalFlexGridLib.MSHFlexGrid MSHFlexGrid2 
      Height          =   1455
      Left            =   120
      TabIndex        =   0
      Top             =   720
      Width           =   5535
      _ExtentX        =   9763
      _ExtentY        =   2566
      _Version        =   393216
      _NumberOfBands  =   1
      _Band(0).Cols   =   2
   End
   Begin VB.Label Label1 
      Caption         =   "发送内容："
      Height          =   255
      Left            =   120
      TabIndex        =   6
      Top             =   2400
      Width           =   975
   End
End
Attribute VB_Name = "Form2"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub Command1_Click()
 vfwebqq1 = GetIniFileString("QQ", "vfwebqq", "", 500, App.path & "\Config.ini") '得到vfwebqq\
 psessionid = GetIniFileString("QQ", "psessionid", "", 1000, App.path & "\Config.ini") '得到vfwebqq
 Get_QQ_State ("http://d1.web2.qq.com/channel/get_online_buddies2?vfwebqq=" & vfwebqq1 & "&clientid=53999199&psessionid=" & psessionid & "&t=1490346100399")
'不加上面的无法发送信息成功。
 Get_QQ_Name
 Timer1.Enabled = True
End Sub

Private Sub Command2_Click()
    On Error Resume Next
    If (Text3.Text = "Gid") Then
    MsgBox "请获取群，然后在双击获取的群名，右下角会出现Gid！", 0, "没有指定群！"
    Exit Sub
    Else
    End If
    vfwebqq1 = GetIniFileString("QQ", "vfwebqq", "", 1000, App.path & "\Config.ini") '得到vfwebqq
    'MsgBox qun_info("http://s.web2.qq.com/api/get_group_info_ext2?gcode=" & Text4.Text & "&vfwebqq=" & vfwebqq1 & "&t=1490337751075")  '//得到群资料。
    psessionid = GetIniFileString("QQ", "psessionid", "", 1000, App.path & "\Config.ini") '得到vfwebqq
    url = "https://d1.web2.qq.com/channel/send_qun_msg2"
    PostDate = "r={""group_uin"":" & Text3.Text & ",""content"":""[\""" & Text2.Text & "\"",[\""font\"",{\""name\"":\""宋体\"",\""size\"":10,\""style\"":[0,0,0],\""color\"":\""000000\""}]]"",""face"":603,""clientid"":53999199,""msg_id"":16680001,""psessionid"":""" & psessionid & """}"
    Webcode = Post_QQ_Message("POST", url, PostDate)
    '判断返回值
    '100100
    Dim ScriptObj As Object
    Set ScriptObj = CreateObject("MSScriptControl.ScriptControl")
    ScriptObj.AllowUI = True
    ScriptObj.Language = "JavaScript"
    ScriptObj.AddCode "var data = " & Webcode & ";"
'立即窗口显示第一个delist_time
    cw = ScriptObj.Eval("data.retcode")
    If (cw = "100100") Then
    MsgBox "发送成功了哟！", 0, "成功！"
    Text2.Text = ""
    Else
    MsgBox "发送失败，错误代码：" & cw, 0, "失败！"
    End If
End Sub

Private Sub Command3_Click()
MsgBox "先获取当前群，得到之后，后面有UID号码，把UID写到最下面的输入框中，在发送内容一栏中写上自己想要发送的内容就好，注意：最多只能刷新二百个群。"
End Sub



'-----------------------------------------------------------
Private Function qun_info(ByVal XmlHttpURL$)  '这个好像并没有啥用。我艹。只是来得到群的资料而已。
    Dim MyXmlhttp
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
        .setTimeouts 50000, 50000, 50000, 50000
            .Open "GET", XmlHttpURL, True
       
        .SetRequestHeader "cookie", "uin=" & p_uin & ";skey=" & sKey & ";p_uin=" & p_uin & ";p_skey=" & p_skey & ";pt4_token=" & pt4_token & ";ptwebqq=" & ptwebqq
        .SetRequestHeader "referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1"

        .Send ("")
        .WaitForResponse                                                        '异步等待
        If MyXmlhttp.Status = 200 Then                                          '成功获取页面
            qun_info = .ResponseText
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


Private Function Post_Message(ByVal XmlHttpURL$)  '这个好像并没有啥用。我艹。只是来得到群的资料而已。
    On Error Resume Next
    Dim MyXmlhttp
    vfwebqq1 = GetIniFileString("QQ", "vfwebqq", "", 500, App.path & "\Config.ini") '得到vfwebqq
    hash = GetIniFileString("QQ", "hash", "", 500, App.path & "\Config.ini") '得到hash
    p_uin = GetIniFileString("QQ", "p_uin", "", 500, App.path & "\Config.ini") '得到hash
    p_skey = GetIniFileString("QQ", "pskey", "", 500, App.path & "\Config.ini") '得到hash
    pt4_token = GetIniFileString("QQ", "pt4_token", "", 500, App.path & "\Config.ini") '得到hash
    ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到hash
    psessionid = GetIniFileString("QQ", "psessionid", "", 500, App.path & "\Config.ini") '得到hash
    sKey = GetIniFileString("QQ", "skey", "", 500, App.path & "\Config.ini") '得到hash
    On Error GoTo wrong
    Set MyXmlhttp = CreateObject("WinHttp.WinHttpRequest.5.1")                  '创建WinHttpRequest对象
    With MyXmlhttp
        .setTimeouts 50000, 50000, 50000, 50000
            .Open "POST", XmlHttpURL, True
       
        .SetRequestHeader "cookie", "uin=" & p_uin & ";skey=" & sKey & ";p_uin=" & p_uin & ";p_skey=" & p_skey & ";pt4_token=" & pt4_token & ";ptwebqq=" & ptwebqq
        .SetRequestHeader "referer", "https://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1"

        .Send ("r={""ptwebqq"":""" & ptwebqq & """,""clientid"":53999199,""psessionid"":""" & psessionid & """,""key"":""""}")
        .WaitForResponse                                                        '异步等待
        If MyXmlhttp.Status = 200 Then                                          '成功获取页面
            Post_Message = .ResponseText
        Else
            MsgBox "Http错误代码:" & .Status, vbInformation, "提示"
        End If
    End With
    Set MyXmlhttp = Nothing
    Exit Function
wrong:
    Set MyXmlhttp = Nothing
End Function

Private Function Get_QQ_State(ByVal XmlHttpURL$) ' 此为获得状态，因为WEBQQ最新改版，这个必须要加。
    Dim MyXmlhttp
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
        .setTimeouts 50000, 50000, 50000, 50000
            .Open "GET", XmlHttpURL, True
       
        .SetRequestHeader "cookie", "uin=" & p_uin & ";skey=" & sKey & ";p_uin=" & p_uin & ";p_skey=" & p_skey & ";pt4_token=" & pt4_token & ";ptwebqq=" & ptwebqq
        .SetRequestHeader "referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2"

        .Send ("")
        .WaitForResponse                                                        '异步等待
        If MyXmlhttp.Status = 200 Then                                          '成功获取页面
            Get_QQ_State = .ResponseText
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

Private Sub Form_Unload(Cancel As Integer)
End
End Sub

Private Sub Label2_Click()

End Sub

Private Sub MSHFlexGrid2_DblClick()
    With MSHFlexGrid2
    Text3.Text = .TextMatrix(.Row, 2)
    End With
End Sub


Private Function GetCookie(Str$)                                                '处理Cookie,传入.getAllResponseHeaders
    Dim cookie$, a&, b&, c$, d&, e&, f$
    a = InStr(Str, "Set-Cookie: ")
    If a = 0 Then
        GetCookie = ""
    Else
        b = InStr(a, Str, ";"): c = Mid(Str, a + 12, b - a - 11)
        cookie = c
        Do
            d = InStr(b, Str, "Set-Cookie: ")
            If d = 0 Then Exit Do
            e = InStr(d, Str, ";"): f = Mid(Str, d + 12, e - d - 11)
            b = e
            cookie = cookie & f
        Loop
        GetCookie = cookie
    End If
End Function


    '调用方法<写在过程或函数里>
    '设定变量Webcode为函数返回值
'-----------------------------------------------------------\
Private Sub Form_Load()
       With MSHFlexGrid2
        .Rows = 30
        .Cols = 4
        .TextMatrix(1, 1) = ""
        .TextMatrix(1, 2) = ""
        .TextMatrix(2, 1) = ""
        .TextMatrix(2, 2) = ""
        .RowHeight(1) = 200
        .RowHeight(2) = 200
        .TextMatrix(0, 1) = "QQ群"
        .TextMatrix(0, 2) = "Gid"
        .TextMatrix(0, 3) = "Code"
        .ColWidth(1) = 1500
        .ColWidth(2) = 1200
        .ColWidth(2) = 1200
        For i = .FixedRows To .Rows - .FixedRows
        .TextMatrix(i, 0) = i
        Next i
    End With
End Sub

Private Sub Timer1_Timer()
On Error Resume Next
a = Post_Message("https://d1.web2.qq.com/channel/poll2")
Message_Json (a)
End Sub
