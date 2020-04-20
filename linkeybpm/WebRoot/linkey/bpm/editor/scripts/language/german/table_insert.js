function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Zeilen";
    txtLang[1].innerHTML = "Zellabstand";
    txtLang[2].innerHTML = "Spalten";
    txtLang[3].innerHTML = "Textabstand";
    txtLang[4].innerHTML = "Rahmen";
    txtLang[5].innerHTML = "kollabieren";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "kein Rahmen";
    optLang[1].text = "Ja";
    optLang[2].text = "Nein";
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";

    document.getElementById("btnSpan1").value = "Span v";
    document.getElementById("btnSpan2").value = "Span >";
    }
function writeTitle()
    {
    document.write("<title>"+"Tabelle einf\u00FCgen"+"</title>")
    }