<td colspan="5">
    <?php echo link_to($intent_handles_extra->getIdIntentHandlesExtra() ? $intent_handles_extra->getIdIntentHandlesExtra() : __('-'), 'IntentHandlesExtra/edit?id_intent_handles_extra='.$intent_handles_extra->getIdIntentHandlesExtra()) ?>
     - 
    <?php echo $intent_handles_extra->getIntentId() ?>
     - 
    <?php echo $intent_handles_extra->getExtraId() ?>
     - 
    <?php echo $intent_handles_extra->getMandatory() ?>
     - 
    <?php echo $intent_handles_extra->getDescription() ?>
     - 
</td>