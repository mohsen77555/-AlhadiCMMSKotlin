package com.alhadi.cmms.data

data class SerialMasterRequest(
    val partId: Long,
    val serialNumber: String,
    val notes: String = ""
)

data class SerializedReceiptRequest(
    val partId: Long,
    val serialNumbers: List<String>,
    val plant: String,
    val storageLocation: String,
    val stockType: String = "Unrestricted",
    val batch: String = "",
    val vendor: String = "",
    val note: String = ""
)

data class SerializedIssueRequest(
    val partId: Long,
    val serialIds: List<Long>,
    val workOrderId: Long? = null,
    val note: String = ""
)

data class SerialTransferRequest(
    val serialId: Long,
    val plant: String,
    val storageLocation: String,
    val stockType: String,
    val batch: String = "",
    val note: String = ""
)

data class SerialInstallRequest(
    val serialId: Long,
    val assetId: Long,
    val note: String = ""
)
