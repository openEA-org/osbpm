function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "St\u00F8rrelse";
    txtLang[1].innerHTML = "Egenskaper";
    txtLang[2].innerHTML = "Typografi";
    txtLang[3].innerHTML = "Bredde";
    txtLang[4].innerHTML = "Bredde styres av innhold";
    txtLang[5].innerHTML = "Tabellbredde";
    txtLang[6].innerHTML = "Tilpass til vindu";
    txtLang[7].innerHTML = "H\u00F8yde";
    txtLang[8].innerHTML = "Bredde styres av innhold";
    txtLang[9].innerHTML = "Tabellbredde";
    txtLang[10].innerHTML = "Tilpass til vindu";
    txtLang[11].innerHTML = "Justering";
    txtLang[12].innerHTML = "Margen";
    txtLang[13].innerHTML = "Venstre";
    txtLang[14].innerHTML = "H\u00F8yre";
    txtLang[15].innerHTML = "Topp";
    txtLang[16].innerHTML = "Nederst";  
    txtLang[17].innerHTML = "Ramme";
    txtLang[18].innerHTML = "Kollapse";
    txtLang[19].innerHTML = "Bakgrunn";
    txtLang[20].innerHTML = "Celle avstand";
    txtLang[21].innerHTML = "Celle margen";
    txtLang[22].innerHTML = "Typografi";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "prosent"
    optLang[2].text = "pixels"
    optLang[3].text = "prosent"
    optLang[4].text = "Venstre"
    optLang[5].text = "Midtstill"
    optLang[6].text = "H\u00F8yre"
    optLang[7].text = "Ingen"
    optLang[8].text = "Ja"
    optLang[9].text = "Nei"

    document.getElementById("btnPick").value="Velg";
    document.getElementById("btnImage").value="Bilde";

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Tabell egenskaper</title>")
    }