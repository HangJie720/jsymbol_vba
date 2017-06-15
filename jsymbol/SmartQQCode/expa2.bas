Public Sub Module()
    Dim sd As Boolean
   
End Sub

Private Sub CommandButton1_Click()  
   Sheet1.Columns("B:B").Select  
    Application.Selection.Delete  
    For q = 1 To TextBox1.Value  
    GetOnerRow (q)  
    Next q  
End Sub  
  
Private Sub CommandButton2_Click()  
    Sheet1.Range("B1: B" + TextBox1.Value).Select  
     Application.Selection.Copy  
       
      
End Sub  
Sub TestClass()
	If Class1 Is Nothing Then Set Class1 = New Arithmetic
      Class1.ClearNumber
	  Class1.Number1 = 9
	  Class1.Number2 = 8
	  Debug.Print Class1.add
	  Debug.Print Class1.sub
	  Debug.Print Class1.mul
	  Debug.Print Class1.div
End Sub

Private Sub Class1_OnError(ByVal Number As Long, ByVal Description As String, ByVal Source As String)
	  MsgBox "ErrorCode：" & Number & "　ErrorMsg：" & Description & " ErrorSource：" & Source
End Sub

Function remRusLetters(valForClean As String) As String
    
    Dim i As Integer, rusLetters As Variant, engLetters As Variant
    Dim cleanedVal As String
    Dim tmpStr As String
    
    cleanedVal = valForClean
    rusLetters = Array("ид", "?", "?", "?", "ик", "и║", "им", "?", "D", "и░", "?", "ио")
    engLetters = Array("A", "B", "C", "E", "H", "K", "M", "O", "P", "T", "X", "Y")
    
    For i = 0 To UBound(rusLetters)

        If InStr(1, valForClean, rusLetters(i)) <> 0 Then
            cleanedVal = Replace(valForClean, rusLetters(i), engLetters(i))
        End If
    
    Next i

    remRusLetters = cleanedVal
End Function

