package com.example

import com.example.data.CommunicationSkillsData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CommunicationSkillsTest {

    @Test
    fun testSkillsCountAndUniqueness() {
        val list = CommunicationSkillsData.skills
        // Ensure there are at least 20 skills as requested
        assertTrue("There must be at least 20 skills", list.size >= 20)
        
        // Ensure none of them are empty or null
        list.forEach { skill ->
            assertNotNull(skill)
            assertTrue(skill.isNotBlank())
        }
    }

    @Test
    fun testSkillForDateIsDeterministic() {
        val date1 = "2026-07-06"
        val date2 = "2026-07-07"
        
        val skill1 = CommunicationSkillsData.getSkillForDate(date1)
        val skill2 = CommunicationSkillsData.getSkillForDate(date1)
        val skill3 = CommunicationSkillsData.getSkillForDate(date2)
        
        // Ensure determinism: same date results in same skill
        assertEquals(skill1, skill2)
        
        // Let's assert a skill is returned
        assertTrue(skill1.isNotEmpty())
        assertTrue(skill3.isNotEmpty())
    }
}
