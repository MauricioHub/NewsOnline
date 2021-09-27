package com.fomat.newsonline.Models

data class News(var author : String, var title : String,
                var description : String, var url : String,
                var source : String, var image : String,
                var category: String, var language : String,
                var country : String, var published_at : String)
