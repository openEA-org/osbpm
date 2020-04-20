function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Imagen";
    txtLang[1].innerHTML = "Texto";
    txtLang[2].innerHTML = "Espacio";
    txtLang[3].innerHTML = "Alinear";
    txtLang[4].innerHTML = "Arriba";
    txtLang[5].innerHTML = "Borde imagen";
    txtLang[6].innerHTML = "Abajo";
    txtLang[7].innerHTML = "Ancho";
    txtLang[8].innerHTML = "Izquierda";
    txtLang[9].innerHTML = "Alto";
    txtLang[10].innerHTML = "Derecha";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Inferior absoluto";
    optLang[1].text = "Medio absoluto";
    optLang[2].text = "Linea de base";
    optLang[3].text = "Abajo";
    optLang[4].text = "Izquierda";
    optLang[5].text = "medio";
    optLang[6].text = "Derecha";
    optLang[7].text = "Texto superior";
    optLang[8].text = "Arriba";
 
    document.getElementById("btnBorder").value = " Estilo Borde ";
    document.getElementById("btnReset").value = "reset"
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Imagen</title>")
    }