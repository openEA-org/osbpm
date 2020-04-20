function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Rader";
    txtLang[1].innerHTML = "Mellanrum";
    txtLang[2].innerHTML = "Kolumner";
    txtLang[3].innerHTML = "Utfyllnad";
    txtLang[4].innerHTML = "Kantlinje";
    txtLang[5].innerHTML = "Cellavst\u00E5nd";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Ingen";
    optLang[1].text = "Ja";
    optLang[2].text = "Nej";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";

    document.getElementById("btnSpan1").value = " v ";
    document.getElementById("btnSpan2").value = " > ";
    }
function writeTitle()
    {
    document.write("<title>Infoga tabell</title>")
    }