jQuery(document).ready(function($){
    let username = $('#username')
    let password = $('#password')
    let name = $('#name')

    $('#regForm').submit(function (event){
        let code = $('#code')

        $('#serverError').hide()

        if(!username.val().includes('@') || username.val().includes(' ')){
            $("label[for='" + username.attr('id') + "']").text("Email содержит неверный формат.")
        } else $("label[for='" + username.attr('id') + "']").text('')

        if (password.val().length < 6 || password.val().length > 24){
            event.preventDefault()

            $("label[for='" + password.attr('id') + "']")
                .text("Пароль должен содержать от 6 до 24 символов (сейчас " + password.val().length + ").")
        } else $("label[for='" + password.attr('id') + "']").text('')
        if(password.val().includes(' '))
            $("label[for='" + password.attr('id') + "']").text("Пароль имеет неверный формат.")

        if (name.val().length < 2 || name.val().length > 64){
            event.preventDefault()

            $("label[for='" + name.attr('id') + "']")
                .text("Имя должно содержать от 2 до 64 символов (сейчас " + name.val().length + ").")
        } else $("label[for='" + name.attr('id') + "']").text('')

        if(code.val().length > 64){
            event.preventDefault()

            $("label[for='" + code.attr('id') + "']")
                .text("Поле может содержать до 64 символов (сейчас " + code.val().length + ").")
        } else $("label[for='" + code.attr('id') + "']").text('')
    });

    $('#addIdeaForm').submit(function (event) {
        let title = $('#title')
        let description = $('#description')

        if(title.val().length < 8 || title.val().length > 64){
            event.preventDefault()

            $("label[for='" + title.attr('id') + "']")
                .text("Заголовок должен содержать от 8 до 64 символов (сейчас " + title.val().length + ").")
        } else $("label[for='" + title.attr('id') + "']").text('')

        if(description.val().length < 64 || description.val().length > 1024){
            event.preventDefault()

            $("label[for='" + description.attr('id') + "']")
                .text("Описание должно содержать от 64 до 1024 символов (сейчас " + description.val().length + ").")
        } else $("label[for='" + description.attr('id') + "']").text('')
    });

    $('#editProfileForm').submit(function (event) {
        let phone = $('#phone')
        let birthday = $('#birthday')
        let lang = $('#lang')
        let about = $('#about')

        if (name.val().length < 2 || name.val().length > 64){
            event.preventDefault()

            $("label[for='" + name.attr('id') + "']")
                .text("Имя должно содержать от 2 до 64 символов (сейчас " + name.val().length + ").")
        } else $("label[for='" + name.attr('id') + "']").text('')

        if (phone.val().length > 25){
            event.preventDefault()

            $("label[for='" + phone.attr('id') + "']")
                .text("Телефон должен содержать до 25 цифр (сейчас " + phone.val().length + ").")
        } else $("label[for='" + phone.attr('id') + "']").text('')

        if (lang.val().length > 1024){
            event.preventDefault()

            $("label[for='" + lang.attr('id') + "']")
                .text("Поле не может содержать более 1024 символов (сейчас " + lang.val().length + ").")
        } else $("label[for='" + lang.attr('id') + "']").text('')

        if (about.val().length > 1024){
            event.preventDefault()

            $("label[for='" + about.attr('id') + "']")
                .text("Поле не может содержать более 1024 символов (сейчас " + about.val().length + ").")
        } else $("label[for='" + about.attr('id') + "']").text('')
    });
});