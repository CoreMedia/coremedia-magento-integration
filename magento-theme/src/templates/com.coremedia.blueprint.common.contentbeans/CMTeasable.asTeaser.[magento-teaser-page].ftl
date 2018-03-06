<#assign isLast=cm.localParameter("islast", false)/>
<@cm.include self=self view="teaser" params={
"isLast": isLast,
"renderTeaserTitle": true,
"renderTeaserText": true,
"renderDimmer": false
}/>
