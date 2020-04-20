function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Rijen";
    txtLang[1].innerHTML = "Ruimte";
    txtLang[2].innerHTML = "Kolommen";
    txtLang[3].innerHTML = "Opvulling";
    txtLang[4].innerHTML = "Randen";
    txtLang[5].innerHTML = "Samenvouwen";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Geen rand";
    optLang[1].text = "Ja";
    optLang[2].text = "Nee";
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    
    document.getElementById("btnSpan1").value = "Span v";
    document.getElementById("btnSpan2").value = "Span >";
    }
function writeTitle()
    {
    document.write("<title>Tabel Invoegen</title>")
    }