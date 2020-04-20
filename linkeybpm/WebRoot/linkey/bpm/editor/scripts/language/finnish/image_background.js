function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Kuvan l\u00E4hde";
    txtLang[1].innerHTML = "Toista";
    txtLang[2].innerHTML = "Keskit\u00E4 vaakasuoraan";
    txtLang[3].innerHTML = "Keskit\u00E4 pystysuoraan";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Toista"
    optLang[1].text = "Ei toistoa"
    optLang[2].text = "Toista vaakasuoraan"
    optLang[3].text = "Toista pystysuoraan"
    optLang[4].text = "Vasen"
    optLang[5].text = "Keskell\u00E4"
    optLang[6].text = "Oikea"
    optLang[7].text = "Pikselit"
    optLang[8].text = "Prosentti"
    optLang[9].text = "Yl\u00E4reuna"
    optLang[10].text = "Keskell\u00E4"
    optLang[11].text = "Alareuna"
    optLang[12].text = "Pikselit"
    optLang[13].text = "Prosentti"
    
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Taustakuva</title>")
    }

