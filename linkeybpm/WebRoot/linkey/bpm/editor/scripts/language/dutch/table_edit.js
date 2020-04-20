function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Passend";
    txtLang[1].innerHTML = "Eigenschappen";
    txtLang[2].innerHTML = "Stijl";
    txtLang[3].innerHTML = "Breedte";
    txtLang[4].innerHTML = "Passend aan inhoud";
    txtLang[5].innerHTML = "Vaste tabel breedte";
    txtLang[6].innerHTML = "Passend aan venster";
    txtLang[7].innerHTML = "Hoogte";
    txtLang[8].innerHTML = "Passend aan inhoud";
    txtLang[9].innerHTML = "Vaste tabel hoogte";
    txtLang[10].innerHTML = "Passend aan venster";
    txtLang[11].innerHTML = "Uitlijning";
    txtLang[12].innerHTML = "Marge";
    txtLang[13].innerHTML = "Links";
    txtLang[14].innerHTML = "Rechts";
    txtLang[15].innerHTML = "Boven";
    txtLang[16].innerHTML = "Onder";    
    txtLang[17].innerHTML = "Randen";
    txtLang[18].innerHTML = "Samenvouwen";
    txtLang[19].innerHTML = "Achtergrond";
    txtLang[20].innerHTML = "CelTussenruimte";//Cell Spacing
    txtLang[21].innerHTML = "Opvulling";//Cell Padding
    txtLang[22].innerHTML = "CSS Tekst";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "procent"
    optLang[2].text = "pixels"
    optLang[3].text = "procent"
    optLang[4].text = "links"
    optLang[5].text = "midden"
    optLang[6].text = "rechts"
    optLang[7].text = "Geen Rand"
    optLang[8].text = "Ja"
    optLang[9].text = "Nee"

    document.getElementById("btnPick").value="Kiezen";
    document.getElementById("btnImage").value="Afbeelding";

    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
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
    document.write("<title>Tabel Eigenschappen</title>")
    }