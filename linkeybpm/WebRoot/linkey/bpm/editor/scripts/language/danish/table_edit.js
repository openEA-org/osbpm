function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "St\u00F8rrelse";
    txtLang[1].innerHTML = "Egenskaber";
    txtLang[2].innerHTML = "Typografi";
    txtLang[3].innerHTML = "Bredde";
    txtLang[4].innerHTML = "Bredde styret af indhold";
    txtLang[5].innerHTML = "Tabelbredde";
    txtLang[6].innerHTML = "Tilpas til vindue";
    txtLang[7].innerHTML = "H\u00F8jde";
    txtLang[8].innerHTML = "Bredde styret af indhold";
    txtLang[9].innerHTML = "Tabelbredde";
    txtLang[10].innerHTML = "Tilpas til vindue";
    txtLang[11].innerHTML = "Justering";
    txtLang[12].innerHTML = "Margen";
    txtLang[13].innerHTML = "Venstre";
    txtLang[14].innerHTML = "H\u00F8jre";
    txtLang[15].innerHTML = "Top";
    txtLang[16].innerHTML = "Nederst";  
    txtLang[17].innerHTML = "Ramme";
    txtLang[18].innerHTML = "Collapse";
    txtLang[19].innerHTML = "Baggrund";
    txtLang[20].innerHTML = "Celle afstand";
    txtLang[21].innerHTML = "Celle margen";
    txtLang[22].innerHTML = "Typografi";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "procent"
    optLang[2].text = "pixels"
    optLang[3].text = "procent"
    optLang[4].text = "Venstre"
    optLang[5].text = "Centrer"
    optLang[6].text = "H\u00F8jre"
    optLang[7].text = "Ingen"
    optLang[8].text = "Ja"
    optLang[9].text = "Nej"

    document.getElementById("btnPick").value="V\u00E6lg";
    document.getElementById("btnImage").value="Billede";

    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Egne farver";
        case "More Colors...": return "Flere farver...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Tabel egenskaber</title>")
    }