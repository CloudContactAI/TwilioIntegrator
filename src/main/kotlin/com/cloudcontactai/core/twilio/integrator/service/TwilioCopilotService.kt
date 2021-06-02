/* Copyright 2021 Cloud Contact AI, Inc. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.cloudcontactai.core.twilio.integrator.service

import com.cloudcontactai.core.twilio.integrator.dto.CopilotResponseDto
import com.cloudcontactai.core.twilio.integrator.dto.CreateCopilotDto
import com.twilio.base.ResourceSet
import com.twilio.rest.messaging.v1.service.PhoneNumber
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service


@Service
@DependsOn("twilioConfig")
class TwilioCopilotService {

    @Value("\${twilio.defaultSmsUrl}")
    lateinit var defaultSmsUrl: String

    @Value("\${twilio.test}")
    var testMode: Boolean = true

    fun createCopilot(createCopilotDto: CreateCopilotDto): CopilotResponseDto {
        val serviceId = this.createMessageService(createCopilotDto.friendlyName, createCopilotDto.callBackUrl)
        val copilotPhones = this.addPhones(serviceId, createCopilotDto.twilioPhonesId)
        return CopilotResponseDto(serviceId, copilotPhones)
    }

    fun updatePhones(serviceId: String, twilioPhonesId: List<String>) {
        val phonesInService = this.listPhonesInMessageService(serviceId)
        if (phonesInService != null) {
            val phonesInServiceId =  phonesInService.map { it.sid }
            phonesInServiceId.forEach {
                if (!twilioPhonesId.contains(it)) {
                    this.deletePhoneNumberFromMessageService(serviceId, it)
                }
            }
            twilioPhonesId.forEach {
                if (!phonesInServiceId.contains(it)) {
                   this.addPhoneNumberToMessageService(serviceId, it)
                }
            }
        }
    }

    fun addPhones(serviceId: String, twilioPhonesId: List<String>): List<String> {
        val twilioPhones = ArrayList<String>()
        twilioPhonesId.forEach {
            val phoneId = addPhoneNumberToMessageService(serviceId, it)
            twilioPhones.add(phoneId)
        }
       return twilioPhones.toList()
    }

    fun deleteMessageService(serviceId: String) {
        if (testMode) return
        com.twilio.rest.messaging.v1.Service.deleter(serviceId).delete()
    }

    private fun createMessageService(friendlyName: String, callBackUrl : String?): String {
        if (testMode) return RandomStringUtils.randomAlphabetic(32)
        /** TODO: Add to the create service a callBackUrl */
        // .setStatusCallback(URI.create("http://requestb.in/1234abcd"))
        val service = com.twilio.rest.messaging.v1.Service.creator(friendlyName)
                .setInboundRequestUrl(defaultSmsUrl)
                .create()
        return service.sid
    }

   private fun addPhoneNumberToMessageService(twilioServiceId: String, twilioPhoneNumberId: String): String {
       if (testMode) return twilioPhoneNumberId
        val phoneNumber = com.twilio.rest.messaging.v1.service.PhoneNumber.creator(
                twilioServiceId, twilioPhoneNumberId)
                .create()
        return phoneNumber.sid
    }

    private fun deletePhoneNumberFromMessageService(twilioServiceId: String, twilioPhoneNumberId: String) {
        if (testMode) return
        com.twilio.rest.messaging.v1.service.PhoneNumber.deleter(
                twilioServiceId, twilioPhoneNumberId)
                .delete()
    }

    private fun listPhonesInMessageService(twilioServiceId: String): ResourceSet<PhoneNumber>? {
        if (testMode) return null
        return PhoneNumber.reader(twilioServiceId).read()
    }

}
