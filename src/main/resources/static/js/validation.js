jQuery(document).ready(function($){
    let username = $('#username')
    let password = $('#password')
    let name = $('#name')
    let division = $('#division')

    $('#regForm').submit(function (event){
        let code = $('#code')

        $('#serverError').hide()

        if(!username.val().includes('@') || username.val().trim().includes(' ')){
            $('#username-error').text("Email содержит неверный формат.")
        } else $('#username-error').text('')

        if (password.val().trim().length < 6 || password.val().trim().length > 24){
            event.preventDefault()

            $('#password-error').text("Пароль должен содержать от 6 до 24 символов (сейчас " + password.val().length + ").")
        } else $('#password-error').text('')

        if(password.val().trim().includes(' '))
            $('#password-error').text("Пароль имеет неверный формат.")

        if (name.val().trim().length < 2 || name.val().trim().length > 64){
            event.preventDefault()

            $('#name-error').text("Имя должно содержать от 2 до 64 символов (сейчас " + name.val().trim().length + ").")
        } else $('#name-error').text('')

        if(code.val().trim().length > 64){
            event.preventDefault()

            $('#code-error').text("Поле может содержать до 64 символов (сейчас " + code.val().trim().length + ").")
        } else $('#code-error').text('')

        if(division.val() === "Подразделение"){
            event.preventDefault()

            $('#division-error').text("Выберите подразделение")
        } else $('#division-error').text('')
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

        if (phone.val().trim().length > 25){
            event.preventDefault()

            $("label[for='" + phone.attr('id') + "']")
                .text("Телефон должен содержать до 25 цифр (сейчас " + phone.val().trim().length + ").")
        } else $("label[for='" + phone.attr('id') + "']").text('')

        if (lang.val().length > 1024){
            event.preventDefault()

            $("label[for='" + lang.attr('id') + "']")
                .text("Поле не может содержать более 1024 символов (сейчас " + lang.val().length + ").")
        } else $("label[for='" + lang.attr('id') + "']").text('')

        if (about.val().trim().length > 1024){
            event.preventDefault()

            $("label[for='" + about.attr('id') + "']")
                .text("Поле не может содержать более 1024 символов (сейчас " + about.val().trim().length + ").")
        } else $("label[for='" + about.attr('id') + "']").text('')
    });
});