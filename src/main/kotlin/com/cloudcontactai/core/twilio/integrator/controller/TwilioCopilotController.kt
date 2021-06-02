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

import com.cloudcontactai.core.twilio.integrator.dto.CopilotPhonesDto
import com.cloudcontactai.core.twilio.integrator.dto.CopilotResponseDto
import com.cloudcontactai.core.twilio.integrator.dto.CreateCopilotDto
import com.cloudcontactai.core.twilio.integrator.service.TwilioCopilotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping(path = ["/api/v1/copilot"])
class TwilioCopilotController {

    @Autowired
    lateinit var twilioCopilotService: TwilioCopilotService


    @PostMapping("/create")
    fun createCopilot(@RequestBody createCopilotDto: CreateCopilotDto): CopilotResponseDto {
        return this.twilioCopilotService.createCopilot(createCopilotDto)
    }

    @PutMapping("/updatePhones")
    fun updatePhonesToCopilot(@RequestBody copilotPhonesDto: CopilotPhonesDto) {
        return this.twilioCopilotService.updatePhones(copilotPhonesDto.twilioServiceId, copilotPhonesDto.twilioPhoneNumbersId)
    }

    @DeleteMapping("/delete/{service_id}")
    fun deleteCopilot(@PathVariable service_id: String) {
        this.twilioCopilotService.deleteMessageService(service_id)
    }

}
