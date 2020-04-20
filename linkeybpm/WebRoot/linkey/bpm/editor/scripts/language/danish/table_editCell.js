function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "St\u00F8rrelse";
    txtLang[1].innerHTML = "Egenskaber";
    txtLang[2].innerHTML = "Typografi";
    txtLang[3].innerHTML = "Bredde";
    txtLang[4].innerHTML = "Bredde styret af indhold";
    txtLang[5].innerHTML = "Bredde";
    txtLang[6].innerHTML = "H\u00F8jde";
    txtLang[7].innerHTML = "Bredde styret af indhold";
    txtLang[8].innerHTML = "H\u00F8jde";
    txtLang[9].innerHTML = "Lodret justering";
    txtLang[10].innerHTML = "Margen";
    txtLang[11].innerHTML = "Venstre";
    txtLang[12].innerHTML = "H\u00F8jre";
    txtLang[13].innerHTML = "Top";
    txtLang[14].innerHTML = "Nederst";
    txtLang[15].innerHTML = "Mellemrum";
    txtLang[16].innerHTML = "Baggrund";
    txtLang[17].innerHTML = "Eksempel";
    txtLang[18].innerHTML = "Typografi";
    txtLang[19].innerHTML = "Overf\u00F8r til";

    document.getElementById("btnPick").value = "V\u00E6lg";
    document.getElementById("btnImage").value = "Billede";
    document.getElementById("btnText").value = " Tekst typografi ";
    document.getElementById("btnBorder").value = " Ramme typografi ";

    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "procent"
    optLang[2].text = "pixels"
    optLang[3].text = "procent"
    optLang[4].text = "Standard"
    optLang[5].text = "\u00D8verst"
    optLang[6].text = "Midt"
    optLang[7].text = "Nederst"
    optLang[8].text = "Grundlinie"
    optLang[9].text = "S\u00F8nket"
    optLang[10].text = "H\u00E6vet"
    optLang[11].text = "Tekst top"
    optLang[12].text = "Tekst nederst"
    optLang[13].text = "Standard"
    optLang[14].text = "Venstre"
    optLang[15].text = "Centrer"
    optLang[16].text = "H\u00F8jre"
    optLang[17].text = "Udj\u00E6vn"
    optLang[18].text = "Ingen"
    optLang[19].text = "Ingen linieskift"
    optLang[20].text = "Pre"
    optLang[21].text = "Normal"
    optLang[22].text = "Aktuelle celle"
    optLang[23].text = "Aktuelle r\u00E6kke"
    optLang[24].text = "Aktuelle kolonne"
    optLang[25].text = "Whole Table"
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Egne farver";
        case "More Colors...": return "Flere farver...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Celle egenskaber</title>")
    }