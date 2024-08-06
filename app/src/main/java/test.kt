class Company {
    var name: String = ""
    var age: String = ""
    var cnic: String = ""


}

fun main() {


    val details=Company().apply {

        name = "Zain"
        age = "22"
        cnic = "3820894820830"


    }

    details.let{
        println(it.name)
        println(it.age)
        println(it.cnic)

    }




}