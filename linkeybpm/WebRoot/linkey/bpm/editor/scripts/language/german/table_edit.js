function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "AutoAnpassen";
    txtLang[1].innerHTML = "Eigenschaften";
    txtLang[2].innerHTML = "Stil";
    txtLang[3].innerHTML = "Breite";
    txtLang[4].innerHTML = "AutoAnpassen an Inhalt";
    txtLang[5].innerHTML = "Feste Tabellenbreite";
    txtLang[6].innerHTML = "AutoAnpassen an Fenster";
    txtLang[7].innerHTML = "H&ouml;he";
    txtLang[8].innerHTML = "AutoAnpassen an Inhalt";
    txtLang[9].innerHTML = "Feste Tabllenh&ouml;he";
    txtLang[10].innerHTML = "AutoAnpassen an Fenster";
    txtLang[11].innerHTML = "Ausrichtung";
    txtLang[12].innerHTML = "Abstand";
    txtLang[13].innerHTML = "links";
    txtLang[14].innerHTML = "rechts";
    txtLang[15].innerHTML = "oben";
    txtLang[16].innerHTML = "unten";    
    txtLang[17].innerHTML = "Rahmen";
    txtLang[18].innerHTML = "kollabieren";
    txtLang[19].innerHTML = "Hintergrund";
    txtLang[20].innerHTML = "Zellenabstand";
    txtLang[21].innerHTML = "Abstand vom Text";
    txtLang[22].innerHTML = "CSS Text";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Pixel"
    optLang[1].text = "Prozent"
    optLang[2].text = "Pixel"
    optLang[3].text = "Prozent"
    optLang[4].text = "links"
    optLang[5].text = "zentriert"
    optLang[6].text = "rechts"
    optLang[7].text = "kein Rahmen"
    optLang[8].text = "Ja"
    optLang[9].text = "Nein"

    document.getElementById("btnPick").value = "w\u00E4hlen";
    document.getElementById("btnImage").value = "Bild";

    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Benutzerfarben";
        case "More Colors...": return "weitere Farben...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Tabelle Eigenschaften</title>")
    }