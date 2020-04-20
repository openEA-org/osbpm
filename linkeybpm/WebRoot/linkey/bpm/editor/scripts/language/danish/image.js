function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Alternativ Tekst";
    txtLang[2].innerHTML = "Afstand";
    txtLang[3].innerHTML = "Justering"; 
    txtLang[4].innerHTML = "Top";
    txtLang[5].innerHTML = "Ramme"; 
    txtLang[6].innerHTML = "Nederst";
    txtLang[7].innerHTML = "Bredde";
    txtLang[8].innerHTML = "Venstre";
    txtLang[9].innerHTML = "H\u00F8jde"
    txtLang[10].innerHTML = "H&#248;jre";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Abs. nederst";
    optLang[1].text = "Abs. midte";
    optLang[2].text = "Grundlinie";
    optLang[3].text = "Nederst";
    optLang[4].text = "Venstre";
    optLang[5].text = "Midt";
    optLang[6].text = "H\u00F8jre";
    optLang[7].text = "Top af tekst";
    optLang[8].text = "&Oslash;verst";
 
    document.getElementById("btnBorder").value = " Ramme typografi ";
    document.getElementById("btnReset").value = "reset"
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t"
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Billede</title>")
    }