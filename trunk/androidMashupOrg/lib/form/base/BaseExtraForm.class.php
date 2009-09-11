<?php

/**
 * Extra form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseExtraForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_extra'     => new sfWidgetFormInputHidden(),
      'name'         => new sfWidgetFormInput(),
      'description'  => new sfWidgetFormInput(),
      'extraType_id' => new sfWidgetFormPropelChoice(array('model' => 'Extratype', 'add_empty' => false)),
    ));

    $this->setValidators(array(
      'id_extra'     => new sfValidatorPropelChoice(array('model' => 'Extra', 'column' => 'id_extra', 'required' => false)),
      'name'         => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'description'  => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'extraType_id' => new sfValidatorPropelChoice(array('model' => 'Extratype', 'column' => 'id_extraType')),
    ));

    $this->widgetSchema->setNameFormat('extra[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Extra';
  }


}
