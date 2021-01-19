jQuery(document).ready(function($) {

    let oldImages = []
    let oldFiles = []

    $('.oldImage').click(function () {
        oldImages.push($(this).attr('data-key'))
        this.remove()
    })

    $('.oldFile').click(function () {
        oldFiles.push($(this).find('img').attr('data-key'))
        this.remove()
    })

    const dt = new DataTransfer()
    const dtf = new DataTransfer()

    function loadImage(input) {

        if (input.files && input.files[0]) {

            for(const file of input.files){

                let ext = file.name.match(/\.([^\.]+)$/)[1];

                switch (ext) {
                    case 'jpg':
                    case 'jpeg':
                    case 'png':
                        break;
                    default:
                        continue;
                }

                dt.items.add(file)

                let reader = new FileReader();

                reader.onload = function(e) {
                    $('.images').append("<img id='image' src=" + e.target.result + " />");
                }

                reader.readAsDataURL(file); // convert to base64 string
            }

            input.files = dt.files
        }
    }

    function loadFiles(input) {

        if (input.files && input.files[0]) {

            for(const file of input.files){

                let ext = file.name.match(/\.([^\.]+)$/)[1];

                switch (ext) {
                    case 'xlsx':
                    case 'xls':
                        $('.files').append("<div class='col-md-2'><img src='/static/images/excel.png' id='image' /><p>" + file.name + "</p></div>")
                        break;
                    case 'doc':
                    case 'docx':
                        $('.files').append("<div class='col-md-2'><img src='/static/images/word.png' id='image' /><p>" + file.name + "</p></div>")
                        break;
                    case 'ppt':
                    case 'pptx':
                        $('.files').append("<div class='col-md-2'><img src='/static/images/powerpoint.png' id='image' /><p>" + file.name + "</p></div>")
                        break;
                    case 'pdf':
                        $('.files').append("<div class='col-md-2'><img src='/static/images/pdf.png' id='image' /><p>" + file.name + "</p></div>")
                        break;
                    case 'xml':
                    case 'txt':
                        $('.files').append("<div class='col-md-2'><img src='/static/images/file.png' id='image' /><p>" + file.name + "</p></div>")
                        break;
                    default:
                        continue;
                }

                dtf.items.add(file)
            }

            input.files = dtf.files
        }
    }

    $('.images').on('click', '#image', function () {
        dt.items.remove(this)
        this.remove()
    })

    $('.files').on('click', 'div', function () {
        dtf.items.remove(this)
        this.remove()
    })

    $('#partImages').change(function() {
        loadImage(this);
    });

    $('#partFiles').change(function() {
        loadFiles(this);
    });

    $('#addIdeaForm').submit(function (event) {
        event.preventDefault()

        let title = $('#title')
        let description = $('#description')

        // Валидация формы

        if(title.val().length < 8 || title.val().length > 64){

            $("label[for='" + title.attr('id') + "']")
                .text("Заголовок должен содержать от 8 до 64 символов (сейчас " + title.val().length + ").")
            return false
        } else $("label[for='" + title.attr('id') + "']").text('')

        if(description.val().trim().length < 64 || description.val().trim().length > 1024){

            $("label[for='" + description.attr('id') + "']")
                .text("Описание должно содержать от 64 до 1024 символов (сейчас " + description.val().length + ").")
            return false
        } else $("label[for='" + description.attr('id') + "']").text('')

        $('#formButton').attr('disabled', true)

        // Формирование пакета данных

        const formData = new FormData()
        formData.append("title", title.val())
        formData.append("description", description.val())
        for(const file of dt.files)
            formData.append("images", file)
        for(const file of dtf.files)
            formData.append("files", file)

        // Отправка данных на сервер

        $.ajax ({
            url: "/ideas/add",
            method: "POST",
            data: formData,
            dataType: 'html',
            processData: false,
            contentType: false,
            success: function() {
                location.href = "/ideas"
            },
            error: function() {
                alert("Возникла ошибка при сохранении идеи.\nОбратитесь за помощью в поддержку.")
                $('#formButton').attr('disabled', false)
            }
        });
    })

    $('#editForm').submit(function (event) {
        event.preventDefault()

        let id = $('#id')
        let title = $('#title')
        let description = $('#description')

        // Валидация формы

        if(title.val().length < 8 || title.val().length > 64){

            $("label[for='" + title.attr('id') + "']")
                .text("Заголовок должен содержать от 8 до 64 символов (сейчас " + title.val().length + ").")
            return false
        } else $("label[for='" + title.attr('id') + "']").text('')

        if(description.val().length < 64 || description.val().length > 1024){

            $("label[for='" + description.attr('id') + "']")
                .text("Описание должно содержать от 64 до 1024 символов (сейчас " + description.val().length + ").")
            return false
        } else $("label[for='" + description.attr('id') + "']").text('')

        // Формирование пакета данных

        const formData = new FormData()
        formData.append("title", title.val())
        formData.append("description", description.val())
        for(const file of dt.files)
            formData.append("images", file)
        for(const file of dtf.files)
            formData.append("files", file)
        for(let file of oldImages)
            formData.append("remImages", file)
        for(let file of oldFiles)
            formData.append("remFiles", file)

        // Отправка данных на сервер

        $.ajax ({
            url: "/ideas/edit-" + id.val(),
            method: "POST",
            data: formData,
            dataType: 'html',
            processData: false,
            contentType: false,
            success: function() {
                location.href = "/ideas/idea-" + id.val()
            },
            error: function(e) {
                console.log(e)
                alert("Возникла ошибка при сохранении идеи.\nОбратитесь за помощью в поддержку.")
            }
        });
    })

    $(document).ajaxSend(function(e, xhr, options) {
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        xhr.setRequestHeader(header, token);
    });
});