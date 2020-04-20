function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Dateipfad";
    txtLang[1].innerHTML = "Hintergrund";
    txtLang[2].innerHTML = "Breite";
    txtLang[3].innerHTML = "H&ouml;he";
    txtLang[4].innerHTML = "Qualit&auml;t";
    txtLang[5].innerHTML = "Ausrichtung";
    txtLang[6].innerHTML = "Wiederholen";
    txtLang[7].innerHTML = "Ja";
    txtLang[8].innerHTML = "Nein";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "PluginsSeite";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "niedrig"
    optLang[1].text = "hoch"
    optLang[2].text = "<keine>"
    optLang[3].text = "links"
    optLang[4].text = "rechts"
    optLang[5].text = "oben"
    optLang[6].text = "unten"
    
    document.getElementById("btnPick").value = "w\u00e4hlen"; //"Pick";//"Pick";
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Benutzerfarben";
        case "More Colors...": return "weitere Farben...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>"+ "Flash einf\u00FCgen" +"</title>")
    }