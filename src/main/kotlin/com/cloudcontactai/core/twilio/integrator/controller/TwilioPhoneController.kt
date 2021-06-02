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

package com.cloudcontactai.core.twilio.integrator.controller

import com.cloudcontactai.core.twilio.integrator.dto.BuyPhoneRequestDto
import com.cloudcontactai.core.twilio.integrator.dto.PhoneNumberDto
import com.cloudcontactai.core.twilio.integrator.service.TwilioPhoneService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping(path = ["/api/v1/phones"])
class TwilioPhoneController {

    @Autowired
    lateinit var twilioPhoneService: TwilioPhoneService

    @GetMapping("/search/{country}")
    fun search(@PathVariable country: String,
               @RequestParam(required = false, defaultValue = "-1") areaCode: Int,
               @RequestParam(required = false, defaultValue = "20") limit: Long): List<PhoneNumberDto> {
        return twilioPhoneService.findPhone(areaCode, country, limit)
    }

    @PostMapping("/buy")
    fun buy(@RequestBody phoneRequest: BuyPhoneRequestDto):String{
        return twilioPhoneService.buy(phoneRequest)
    }
}
