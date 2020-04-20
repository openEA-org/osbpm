function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Dateipfad";
    txtLang[1].innerHTML = "Alternativer Text";
    txtLang[2].innerHTML = "Abstand";
    txtLang[3].innerHTML = "Ausrichtung";
    txtLang[4].innerHTML = "Top";
    txtLang[5].innerHTML = "Bildrahmen";
    txtLang[6].innerHTML = "Bottom";
    txtLang[7].innerHTML = "Breite";
    txtLang[8].innerHTML = "Left";
    txtLang[9].innerHTML = "H&ouml;he";
    txtLang[10].innerHTML = "Right";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Absatz unten";
    optLang[1].text = "Absatz mitte";
    optLang[2].text = "Grundlinie";
    optLang[3].text = "unten";
    optLang[4].text = "links";
    optLang[5].text = "mitte";
    optLang[6].text = "rechts";
    optLang[7].text = "Text oben";
    optLang[8].text = "oben";
 
    document.getElementById("btnBorder").value = " Rahmenstil ";
    document.getElementById("btnReset").value = "reset"
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Bild</title>")
    }