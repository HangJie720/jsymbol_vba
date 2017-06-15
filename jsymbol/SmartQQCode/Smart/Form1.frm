VERSION 5.00
Object = "{EAB22AC0-30C1-11CF-A7EB-0000C05BAE0B}#1.1#0"; "ieframe.dll"
Begin VB.Form Form1 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "SmartQQ"
   ClientHeight    =   3855
   ClientLeft      =   8580
   ClientTop       =   3795
   ClientWidth     =   3015
   Icon            =   "Form1.frx":0000
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   3855
   ScaleWidth      =   3015
   Begin VB.TextBox Text3 
      Height          =   1215
      Left            =   4560
      MultiLine       =   -1  'True
      TabIndex        =   5
      Text            =   "Form1.frx":16AC2
      Top             =   3720
      Width           =   2175
   End
   Begin VB.TextBox Text2 
      Height          =   1575
      Left            =   4560
      MultiLine       =   -1  'True
      TabIndex        =   4
      Text            =   "Form1.frx":16AC8
      Top             =   2160
      Width           =   2175
   End
   Begin VB.Timer Timer1 
      Enabled         =   0   'False
      Interval        =   500
      Left            =   480
      Top             =   840
   End
   Begin SHDocVwCtl.WebBrowser WebBrowser1 
      Height          =   1935
      Left            =   4560
      TabIndex        =   3
      Top             =   240
      Width           =   2175
      ExtentX         =   3836
      ExtentY         =   3413
      ViewMode        =   0
      Offline         =   0
      Silent          =   0
      RegisterAsBrowser=   0
      RegisterAsDropTarget=   1
      AutoArrange     =   0   'False
      NoClientEdge    =   0   'False
      AlignLeft       =   0   'False
      NoWebView       =   0   'False
      HideFileNames   =   0   'False
      SingleClick     =   0   'False
      SingleSelection =   0   'False
      NoFolders       =   0   'False
      Transparent     =   0   'False
      ViewID          =   "{0057D0E0-3573-11CF-AE69-08002B2E1262}"
      Location        =   "http:///"
   End
   Begin VB.CommandButton Command1 
      Caption         =   "我要登陆"
      Height          =   495
      Left            =   1800
      TabIndex        =   1
      Top             =   3240
      Width           =   1095
   End
   Begin VB.Label Label2 
      Caption         =   "请点击->"
      Height          =   255
      Left            =   120
      TabIndex        =   2
      Top             =   3480
      Width           =   1575
   End
   Begin VB.Label Label1 
      Alignment       =   2  'Center
      Caption         =   "第一次登陆"
      Height          =   255
      Left            =   120
      TabIndex        =   0
      Top             =   2760
      Width           =   2775
   End
   Begin VB.Image Image1 
      Height          =   2535
      Left            =   120
      Stretch         =   -1  'True
      Top             =   120
      Width           =   2775
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
  Private Const NO_ERROR = 0
  Private Const INTERNET_OPTION_END_BROWSER_SESSION = 42
  Private Const CSIDL_COOKIES = &H21&
  Private Type SHITEMID
          cb   As Long
          abID   As Byte
  End Type
  Private Type ITEMIDLIST
          mkid   As SHITEMID
  End Type
  Private Declare Function ShellAbout Lib "shell32.dll" Alias "ShellAboutA" (ByVal hwnd As Long, ByVal szApp As String, ByVal szOtherStuff As String, ByVal hIcon As Long) As Long
  Private Declare Function SHGetSpecialFolderLocation Lib "shell32.dll" (ByVal hwndOwner As Long, ByVal nFolder As Long, pidl As ITEMIDLIST) As Long
  Private Declare Function SHGetPathFromIDList Lib "shell32.dll" Alias "SHGetPathFromIDListA" (ByVal pidl As Long, ByVal pszPath As String) As Long
  Private Declare Function InternetSetOption Lib "wininet.dll" Alias "InternetSetOptionA" (ByVal hInternet As Long, ByVal dwOption As Long, ByRef lpBuffer As Any, ByVal dwBufferLength As Long) As Long
  Private Declare Sub Sleep Lib "kernel32" (ByVal dwMilliseconds As Long)
  '以上为清空COOKIEAPI和延时API
  Private Sub Delay(PauseTime As Long)  '延时函数
    Dim Start As Single
    Start = Timer * 1000
    Do While Timer * 1000 < Start + PauseTime
        Sleep (1)
        DoEvents
    Loop
End Sub

'清除COOKIE
Private Function GetSpecialfolder(CSIDL As Long) As String
          Dim r     As Long
          Dim IDL     As ITEMIDLIST
          'Get   the   special   folder
          Dim path     As String
          r = SHGetSpecialFolderLocation(100, CSIDL, IDL)
          If r = NO_ERROR Then
                  'Create   a   buffer
                  path = Space$(512)
                  'Get   the   path   from   the   IDList
                  r = SHGetPathFromIDList(ByVal IDL.mkid.cb, ByVal path$)
                  'Remove   the   unnecessary   chr$(0)'s
                  GetSpecialfolder = left$(path, InStr(path, Chr$(0)) - 1)
                  Exit Function
          End If
          GetSpecialfolder = ""
  End Function
    

Private Sub Command1_Click()
Command1.Caption = "加载..."
Dim Temp_ptwebqq As String '避免类型错误
Dim CtrlRange As Object
Dim Temp As String
Command1.Enabled = False '按纽不可用状态
WebBrowser1.Navigate "https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.22925435146316886"
 Do Until WebBrowser1.ReadyState = 4
DoEvents
Loop
Command1.Caption = "加载成功"
'寻找第二张图片。
Set CtrlRange = WebBrowser1.Document.body.createControlRange
CtrlRange.Add WebBrowser1.Document.images(0) '参数2表示找的第2张图片  针对百度
CtrlRange.execCommand ("Copy")
Image1.Picture = Clipboard.GetData  'Img2
Clipboard.Clear
'刷新验证码结束
Temp = WebBrowser1.Document.cookie '用webbrowser的document对象获取
Midwords = Mid(Temp, 7)
Temp = Midwords  '此处是得到完整的ptwebqq,并写入到TEXT1
SetIniFileString "QQ", "ptwebqq", Temp, App.path & "\Config.ini"
Temp_ptwebqq = Script(Temp) '传递给TEXT ptqrtoken
SetIniFileString "QQ", "ptqrtoken", Temp_ptwebqq, App.path & "\Config.ini" '得到ptqrtoken，此处不知道有没有用。先记下来
Timer1.Enabled = True '开始运行时钟控件
End Sub


Private Sub Form_Load()
  '程序进入之后就清空COOKIES
   On Error Resume Next
    Dim CookiesPath As String
    SkinH_AttachEx App.path & "/Skin/Hacker.she", ""
    CookiesPath = GetSpecialfolder(CSIDL_COOKIES)
    Call InternetSetOption(0, INTERNET_OPTION_END_BROWSER_SESSION, ByVal 0&, 0)
    Kill CookiesPath + "\*.txt"
    Kill "*.ini"
End Sub


Private Sub Timer1_Timer()
On Error Resume Next '出错忽略
Dim One_Loading As String
Dim CheckUrl As String
Dim ptqrtoken As String
ptqrtoken = GetIniFileString("QQ", "ptqrtoken", "", 100, App.path & "\Config.ini") '得到ptqrtoken
One_Loading = ReadinteFile("https://ssl.ptlogin2.qq.com/ptqrlogin?ptqrtoken=" & ptqrtoken & "&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-2096&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10202&login_sig=&pt_randsalt=0")
'注意一下，OneLoding 此处是得到响应信息，登陆的信息。
'下一步就是得到成功登陆信息里面的URL
CheckUrltemp = One_Loading
'处理登陆信息
One_Loading = Replace(One_Loading, "'", "")
Temp_Lable = Split(One_Loading, ",")
Label1.Caption = Temp_Lable(4)
'根据时钟来得到登陆的信息。不断刷新。
'进入二次判断，心好累，自己写的代码我都TM看不懂。
If (Label1.Caption = "登录成功！") Then '上文已经从变量里面已经得到了信息。
Label2.Caption = "二次登陆中.."
CheckUrl = Mid(CheckUrltemp, InStr(1, CheckUrltemp, "http://"), InStr(InStr(1, CheckUrltemp, "http://"), CheckUrltemp, "'") - InStr(1, CheckUrltemp, "http://"))
SetIniFileString "QQ", "CheckUrl", CheckUrl, App.path & "\Config.ini" '写配置CheckUrl
Getsky (CheckUrl) '这一步最重要，如果这个写入不成功，那么将二次登陆也不会成功。
Label2.Caption = "写跳转地址"
'也是重要的一个环节
ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到ptqrtoken
Get_vfwebqq ("http://s.web2.qq.com/api/getvfwebqq?ptwebqq=" & ptwebqq & "&clientid=53999199&psessionid=&t=1490153919298") '写配置vfwebqq
ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到ptqrtoken
Postlogin "http://d1.web2.qq.com/channel/login2", "r={""ptwebqq"":""" & ptwebqq & """,""clientid"":53999199,""psessionid"":"""",""status"":""online""}"
Label2.Caption = "解析处理完成"
Delay 250
'写配置psessionid
'各类参数都已经获取
'开始获得HASH
Label2.Caption = "正在得到正确QQ号" '其实得到正确的QQ号，在某个地址下面有这个正确的COOKIE。
uin = GetIniFileString("QQ", "uin", "", 500, App.path & "\Config.ini") '得到uin
uin = Replace(uin, "o00", "")
If (InStr(1, uin, "o00") = 1) Then
uin = Replace(uin, "o00", "")
Else
If (InStr(1, uin, "o0") = 1) Then
uin = Replace(uin, "o0", "")
ElseIf (InStr(1, uin, "o") = 1) Then
uin = Replace(uin, "o", "")
Else
End If
End If
ptwebqq = GetIniFileString("QQ", "ptwebqq", "", 500, App.path & "\Config.ini") '得到uin
Text2.Text = Text2.Text + "hash(""" & uin & """,""" & ptwebqq & """)"   '加上一句话，执行JS语句。
JScript
Label2.Caption = "正在得到HASH值"
Delay 250
Post_info
Value = Len(Text3.Text)
If (Value > 200) Then
Label2.Caption = "完毕，登陆成功"
Timer1.Enabled = False
Form2.Show
Delay 250
Unload Me
Else
MsgBox "登陆失败,请重新打开软件。", 0, "出错！"
Timer1.Enabled = False
End
End If
End If
End Sub

