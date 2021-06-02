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

import com.cloudcontactai.core.twilio.integrator.dto.BuyPhoneRequestDto
import com.cloudcontactai.core.twilio.integrator.dto.PhoneNumberDto
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber
import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local
import com.twilio.type.PhoneNumber
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service

@Service
@DependsOn("twilioConfig")
class TwilioPhoneService {

    @Value("\${twilio.defaultSmsUrl}")
    lateinit var defaultSmsUrl: String

    @Value("\${twilio.test}")
    var testMode: Boolean = true


    fun findPhone(areaCode: Int, country: String, limit: Long): List<PhoneNumberDto> {
        var reader = Local.reader(country.toUpperCase())
        if (areaCode > 0) {
            reader = reader.setAreaCode(areaCode)
        }
        var phones = reader.limit(limit).read()
        return phones.map<Local, PhoneNumberDto> { phone ->
            PhoneNumberDto(
                friendlyName = phone.friendlyName.toString(),
                phoneNumber = phone.phoneNumber.toString(),
                mms = phone.capabilities.mms,
                sms = phone.capabilities.sms,
                voice = phone.capabilities.voice
            )
        }
    }

    fun buy(request: BuyPhoneRequestDto): String {
        if (testMode) return RandomStringUtils.randomAlphabetic(32)
        var incomingUrl = request.incomingUrl ?: defaultSmsUrl
        var phoneCreator = IncomingPhoneNumber
            .creator(PhoneNumber(request.phone))
            .setSmsUrl(incomingUrl)
            .setFriendlyName(request.friendlyName)
        var result = phoneCreator.create()
        return result.sid;
    }

    fun delete(phones: List<String>) {
        if (testMode) return
        phones.forEach { deleteTwilioPhone(it) }
    }

    private fun deleteTwilioPhone(phoneId: String) {
        try {
            IncomingPhoneNumber.deleter(phoneId).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
