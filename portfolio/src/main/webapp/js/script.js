//When page is loaded
$(document).ready(function() {
    //Fetch comments from backend
    fetchComments();
    //Setup form validation
    $('.ui.form')
    .form({
        fields: {
            author: {
                identifier: 'author',
                rules: [
                    {
                        type   : 'empty',
                        prompt : 'Please enter your name'
                    }
                ]
            },
            comment: {
                identifier: 'comment',
                rules: [
                    {
                        type   : `minLength[${COMMENT_MIN_LENGTH}]`,
                        prompt : `Comment should be atleast ${COMMENT_MIN_LENGTH} characters.`
                    },
                    {
                        type   : `maxLength[${COMMENT_MAX_LENGTH}]`,
                        prompt : `Comment should be atleast ${COMMENT_MAX_LENGTH} characters.`
                    }
                ]
            },
        }
    });
});

//Fetches comments from server
function fetchComments() {
    fetch('/comments')
    .then(response => response.text())
    .then(comments => addCommentsToDOM(JSON.parse(comments)));
}

//Display comments on the page
function addCommentsToDOM(comments) {
    var commentsText = "";
    var now = new Date();

    comments.forEach(comment => {
        commentsText += '<div class="ui comment">';
        commentsText += '<a class="avatar"><img src="https://semantic-ui.com/images/wireframe/square-image.png"></a>';
        commentsText += '<div class="content">';
        commentsText += '<a class="author">' + comment.author + '</a>';

        var d = new Date(comment.timestamp);
        commentsText += '<div class="metadata">';
        commentsText += '<span class="date">';
        if(d.getDate() == now.getDate()) {
            var time12hr = new Intl.DateTimeFormat('default', {
                                                    hour12: true,
                                                    hour: 'numeric',
                                                    minute: 'numeric'
                                                }).format(d);
            commentsText += time12hr;
        } else {
            commentsText += d.getDate() + '/' + (d.getMonth()+1) + '/' + d.getFullYear();
        }
        commentsText += '</span>';
        commentsText += '</div>';

        commentsText += '<div class="text"><p>' + comment.comment + '</p></div>';
        commentsText += '</div>';
        commentsText += '</div>';
        commentsText += '</div>';
    });
    document.getElementById("comments").innerHTML = commentsText;
}
