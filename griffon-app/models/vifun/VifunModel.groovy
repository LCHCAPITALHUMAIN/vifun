/**
 * Copyright 2013  jmlucjav@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
   
package vifun

import groovy.beans.Bindable
import ca.odell.glazedlists.*
import ca.odell.glazedlists.event.*
import ca.odell.glazedlists.gui.*
import ca.odell.glazedlists.swing.*

class VifunModel {
    SolrOps solr = new SolrOps()
    @Bindable String solrurl = "http://localhost:8983/solr/core0"
    Map handlers = [:]

    //selected handler
    Map handlerm = [:]
    @Bindable String handler
    @Bindable String handlerText
    //EventList handlersList = new BasicEventList()

    //current
    List<Map> currentMap = []
    @Bindable String currentParam
    //baseline
    List<Map> baselineMap = []
    @Bindable String baselineParam

    @Bindable String errMsg

    //buttons enabled?
    @Bindable boolean enabledQuery
    //@Bindable boolean enabledTake
    @Bindable boolean enabledBind
    @Bindable boolean enabledSlider
    //buttons enabled?
    @Bindable boolean enabledHandlerText
    @Bindable boolean enabledCurrentParam
    @Bindable boolean enabledBaselineParam
    @Bindable boolean enabledErrMsg
    @Bindable boolean enabledCompare
    //scoring stuff
    @Bindable boolean enabledqf
    @Bindable boolean enabledpf
    @Bindable boolean enabledpf2
    @Bindable boolean enabledpf3
    @Bindable boolean enabledps
    @Bindable boolean enabledps2
    @Bindable boolean enabledps3
    @Bindable boolean enabledboost
    @Bindable boolean enabledmm
    @Bindable boolean enabledtie
    @Bindable boolean enabledbf_0
    @Bindable boolean enabledbf_1
    @Bindable boolean enabledbf_2
    @Bindable boolean enabledbq_0
    @Bindable boolean enabledbq_1
    @Bindable boolean enabledbq_2

    //params
    @Bindable String q
    @Bindable String rows
    @Bindable String fl
    List<String> qset = ['q','rows', 'fl']
    @Bindable String rest
    //edismax score related vars
    @Bindable String qf
    @Bindable String pf
    @Bindable String pf2
    @Bindable String pf3
    @Bindable String ps
    @Bindable String ps2
    @Bindable String ps3
    @Bindable String boost
    @Bindable String mm
    @Bindable String tie
    //several 
    @Bindable String bf_0
    @Bindable String bf_1
    @Bindable String bf_2
    @Bindable String bq_0
    @Bindable String bq_1
    @Bindable String bq_2
    List<String> fset = ['qf','pf','pf2','pf3','ps','ps2','ps3', 'boost', 'mm', 'tie', 'bf_1', 'bf_2', 'bf_0', 'bq_1', 'bq_2', 'bq_0']
    List<String> fmultiple = ['bf', 'bq']
    //associate each bf to bf1...
    Map fbf = [:]
    Map fbq = [:]
    //ignore params
    List<String> fignore = ['facet', 'spellcheck', 'mlt', 'hl', 'v', 'title', 'echoParams']

    //change selected bf...
    @Bindable String tweakedFName
    @Bindable String tweakedFFormula
    @Bindable String tweakedFValue
    //we store the pos to just replace that value
    @Bindable int tweakedFValuePos
    @Bindable String tweakedFValueNewTemp
    @Bindable String tweakedFValueNew
    //scroll
    @Bindable boolean synchScroll = true
    def origCVerScroll, origCHorScroll  

    float maxScoreDiff
    //glazedlist stuff
    def columns = [[name: 'pos', title: 'Rank'],[name: 'posdelta', title: 'Delta'],[name: 'docfields', title: 'Doc'], [name: 'score', title: 'Score'],[name: 'scoredelta', title: 'Delta']]
    def columnsbaseline = [[name: 'pos', title: 'Rank'],[name: 'docfields', title: 'Doc'], [name: 'score', title: 'Score']]
    def deltaPosComparator = {a,b -> return deltaCompare(a.posdelta, b.posdelta)}as Comparator
    def deltaScoreComparator = {a,b -> return deltaCompare(a.scoredelta, b.scoredelta)}as Comparator
    int deltaCompare (String a, String b){
        if ('+'.equals(a) || '+'.equals(b)) {
            log.debug 'one +'
        }
        if (!a && !b) return 0
        if (!a) return -1
        if (!b) return 1
        if ('+'.equals(a) && '+'.equals(b)) return 0
        if ('+'.equals(a)) return 1
        if ('+'.equals(b)) return -1
        return Double.compare(a as Double, b as Double)
    }
    //EventList ctable = new BasicEventList(), {a, b -> a.pos <=> b.pos} as Comparator)
    //EventList btable = new BasicEventList(), {a, b -> a.pos <=> b.pos} as Comparator)
    EventList ctable = new SortedList(new BasicEventList(), {a, b -> a.pos <=> b.pos} as Comparator)
    EventList btable = new SortedList(new BasicEventList(), {a, b -> a.pos <=> b.pos} as Comparator)
    //selected docs 
    @Bindable def bseldoc,cseldoc
    def clistener = { event ->
        log.debug "${event.sourceList}"
        if (event.sourceList[0]){
            def rowIndex = event.sourceList[0].pos
            cseldoc = currentMap[rowIndex]
        }
    } as ListEventListener
    def blistener = { event ->
        log.debug "${event.sourceList}"
        if (event.sourceList[0]){
            def rowIndex = event.sourceList[0].pos
            bseldoc = baselineMap[rowIndex]
        }       
    } as ListEventListener
}
