package com.example.core.model

enum class SearchCategory(val title: String, val description: String) {
    CONSENTED_DIRECTORY("Consented Directory", "Legally collected public registries & verified profiles"),
    ORGANIZATION("Organization Records", "Corporate entities, NGOs, institutions & divisions"),
    AUTHORIZED_CONTACT("Authorized Contacts", "Direct liaison, security officials & verified representatives")
}

enum class VerificationStatus {
    VERIFIED,
    PENDING,
    RESTRICTED
}

data class SearchResult(
    val id: String,
    val category: SearchCategory,
    val displayName: String,
    val designation: String,
    val organization: String,
    val department: String,
    val location: String,
    // Sensitive fields masked by default
    val maskedNationalId: String,
    val maskedPhone: String,
    val maskedEmail: String,
    val maskedAddress: String,
    // Compliance & audit metadata
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val sourceDataset: String = "DuckDB-Parquet / Consented Gov Directory v2.4",
    val consentBasis: String = "Explicit GDPR/DPDP Consented Directory Record",
    val registeredDate: String = "2024-08-15",
    val tags: List<String> = listOf("Verified Entity", "Consented", "Audited"),
    val confidenceScore: Double = 0.98
)
