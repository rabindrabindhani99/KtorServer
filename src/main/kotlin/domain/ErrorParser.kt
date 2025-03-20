package com.rabindradev.domain

object ErrorParser {

    fun parse(errorMessage: String): String {
        return when {
            errorMessage.contains("duplicate key value violates unique constraint") -> {
                val field = extractFieldName(errorMessage)
                "The $field is already in use. Please use a different one."
            }

            errorMessage.contains("violates not-null constraint") -> {
                val field = extractFieldName(errorMessage)
                "The field '$field' cannot be empty."
            }

            errorMessage.contains("syntax error at or near") -> {
                "There is a syntax error in the SQL statement."
            }

            errorMessage.contains("invalid input syntax for type") -> {
                "Invalid input syntax. Please check the data types of your input."
            }

            errorMessage.contains("could not connect to server") -> {
                "Unable to connect to the database server. Please try again later."
            }

            errorMessage.contains("permission denied for relation") -> {
                "Permission denied for the specified database relation."
            }

            errorMessage.contains("division by zero") -> {
                "A division by zero error occurred in the database operation."
            }

            errorMessage.contains("deadlock detected") -> {
                "A deadlock was detected in the database. Please retry the operation."
            }

            errorMessage.contains("out of memory") -> {
                "The database ran out of memory during the operation."
            }

            errorMessage.contains("could not serialize access due to concurrent update") -> {
                "Concurrent update error. Please retry the operation."
            }

            // Ktor Server Errors
            errorMessage.contains("Request timeout") -> {
                "The server timed out waiting for the request. Please try again."
            }

            errorMessage.contains("Connection refused") -> {
                "The server refused the connection. It might be down or misconfigured."
            }

            errorMessage.contains("No transformation found for") -> {
                "The server could not process the request due to an unsupported media type."
            }

            errorMessage.contains("Unresolved reference") -> {
                "A reference in the code could not be resolved. Please check your request."
            }

            errorMessage.contains("ClassCastException") -> {
                "A class cast exception occurred. Please ensure the correct data types are used."
            }

            errorMessage.contains("Missing request parameter") -> {
                "A required request parameter is missing."
            }

            errorMessage.contains("Authentication failed") -> {
                "Authentication failed. Please check your credentials."
            }

            errorMessage.contains("Authorization failed") -> {
                "Authorization failed. You do not have permission to access this resource."
            }

            errorMessage.contains("Resource not found") -> {
                "The requested resource could not be found."
            }

            errorMessage.contains("Internal server error") -> {
                "An internal server error occurred. Please try again later."
            }

            else -> "Unexpected error occurred: $errorMessage"
        }
    }

    private fun extractFieldName(errorMessage: String): String {
        val regex = """Key \((.*?)\)=\(""".toRegex()
        return regex.find(errorMessage)?.groupValues?.get(1) ?: "field"
    }
}
