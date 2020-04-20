function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Paleta Web";
    txtLang[1].innerHTML = "Nombre color";
    txtLang[2].innerHTML = "Web 216";
    txtLang[3].innerHTML = "Nuevo";
    txtLang[4].innerHTML = "Current";//"Ultimo seleccionado"
    txtLang[5].innerHTML = "Custom colors";//"Colores personalizados"
    
    document.getElementById("btnAddToCustom").value = "Add to Custom Colors";//unescape("a%F1adir a colores personalizados")
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnRemove").value = " Eliminar color";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Colores</title>")
    }